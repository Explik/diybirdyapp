import json
import shutil
import tempfile
import sqlite3
import zipfile 
import os

####################################################################################################
## Functions 
####################################################################################################

# Unzips the given archive file to a temporary directory and returns the path to the directory
def extract_files(archive_file_path):
    temp_dir = tempfile.mkdtemp()
    with zipfile.ZipFile(archive_file_path, 'r') as archive:
        archive.extractall(temp_dir)
    return temp_dir

def fetch_database(folder_path): 
    # If collection.anki21 file exists, use it
    anki21_path = os.path.join(folder_path, "collection.anki21")
    if os.path.exists(anki21_path):
        return sqlite3.connect(anki21_path)

    # If collection.anki2 file exists, use it
    anki20_path = os.path.join(folder_path, "collection.anki2")
    if os.path.exists(anki20_path):
        return sqlite3.connect(anki20_path)

    # If neither file exists, raise an error
    raise FileNotFoundError("No valid ANKI database file found in the provided folder.")

def fetch_media_map(folder_path):
    media_file_path = os.path.join(folder_path, "media")
    reverse_map = json.load(open(media_file_path))

    media_map = dict()
    for key, value in reverse_map.items():
        media_map[value] = os.path.join(folder_path, key)

    return media_map   

def get_field_names(db): 
    # Fetch cursor 
    cursor = db.cursor()

    # Collection data is stored inside as a single row the "col" and "models" column table
    result = cursor.execute("SELECT models FROM col LIMIT 1").fetchone()
    
    field_schema = json.loads(result[0])

    # Collection columns
    main_key = next(iter(field_schema))
    flds = field_schema[main_key]["flds"]

    return [x["name"] for x in flds]

# Fetches flashcards from the ANKI database 
def get_flashcards(db) -> list[dict]: 
    # Fetch field names 
    field_names = get_field_names(db)

    # Fetch flashcard fields 
    buffer = list()

    for row in db.cursor().execute("SELECT * FROM notes").fetchall():
        fields = dict()
        field_values = row[6].split("\x1f")

        for i in range(min(len(field_names), len(field_values))):
            field_name = field_names[i]
            field_value = field_values[i]

            fields[field_name] = field_value

        buffer.append(fields)
    
    return buffer

####################################################################################################
## Classes
####################################################################################################
class AnkiDeck: 
    def __init__(self):
        self.database = None
        self.media_map = None
        self.field_names = None
        self.cards = None

    def get_field_names(self) -> list[str]:
        return self.field_names
    
    def get_sound_field_names(self) -> list[str]:
        buffer = list()

        for field_name in self.field_names:
            first_field_value = self.flashcards[0].get_raw_value(field_name)
            if first_field_value and first_field_value.startswith("[sound:"):
                buffer.append(field_name)

        return buffer
    
    def get_image_field_names(self) -> list[str]:
        """Get field names that contain image references (e.g., <img src="file.jpg">)"""
        buffer = list()

        for field_name in self.field_names:
            first_field_value = self.flashcards[0].get_raw_value(field_name)
            if first_field_value and "<img" in first_field_value.lower():
                buffer.append(field_name)

        return buffer
    
    def get_video_field_names(self) -> list[str]:
        """Get field names that contain video references (e.g., <video src="file.mp4">)"""
        buffer = list()

        for field_name in self.field_names:
            first_field_value = self.flashcards[0].get_raw_value(field_name)
            if first_field_value and "<video" in first_field_value.lower():
                buffer.append(field_name)

        return buffer
    
    def get_media_field_names(self) -> list[str]:
        """Get all field names that contain any type of media (sound, image, or video)"""
        return list(set(self.get_sound_field_names() + self.get_image_field_names() + self.get_video_field_names()))

    def get_flashcards(self) -> list['AnkiCard']:
        return self.flashcards
    
    def get_number_of_flashcards(self) -> int:
        return len(self.flashcards)

    def get_media_path(self, media_name): 
        return self.media_map.get(media_name) if media_name in self.media_map else None
    
    def copy_media(self, media_name, target_folder, include_ext=True):
        source_path = self.get_media_path(media_name)
        source_file_name = os.path.basename(source_path)
        target_path = os.path.join(target_folder, source_file_name)
        
        if include_ext: 
            file_ext = media_name.split(".")[1] if "." in media_name else ""
            target_path = target_path + "." + file_ext

        shutil.copy(source_path, target_path)

        return os.path.basename(target_path)

    @staticmethod
    def create_from_file(file_path): 
        instance = AnkiDeck()

        unzipped_file = extract_files(file_path)

        instance.database = fetch_database(unzipped_file)
        instance.field_names = get_field_names(instance.database)
        instance.flashcards = [AnkiCard(instance, n) for n in get_flashcards(instance.database)]

        instance.media_map = fetch_media_map(unzipped_file)

        return instance
    
class AnkiCard: 
    def __init__(self, file: AnkiDeck, flashcard):
        self.file = file
        self.flashcard = flashcard

    def get_media_path(self, field_name, transform=None):
        text_value = self.get_text_value(field_name, transform)
        text_value = text_value.lstrip("[sound:]").rstrip("]")
        media_path = self.file.get_media_path(text_value)
        if not media_path:
            raise ValueError(f"Media file not found for text value {text_value}")

        return media_path
    
    def has_media_path(self, field_name, transform=None):
        text_value = self.get_text_value(field_name, transform)
        media_path = self.file.get_media_path(text_value)
        return media_path is not None
    
    def extract_media_filename(self, field_name) -> str:
        """Extract media filename from Anki field (e.g., [sound:file.mp3] or <img src="file.jpg">)"""
        import re
        raw_value = self.get_raw_value(field_name)
        
        # Check for sound tag: [sound:filename]
        sound_match = re.search(r'\[sound:([^\]]+)\]', raw_value)
        if sound_match:
            return sound_match.group(1)
        
        # Check for img tag: <img src="filename">
        img_match = re.search(r'<img[^>]+src=["\']?([^"\'>\s]+)["\']?', raw_value, re.IGNORECASE)
        if img_match:
            return img_match.group(1)
        
        # Check for video tag: <video src="filename">
        video_match = re.search(r'<video[^>]+src=["\']?([^"\'>\s]+)["\']?', raw_value, re.IGNORECASE)
        if video_match:
            return video_match.group(1)
        
        return None
    
    def get_media_type(self, field_name) -> str:
        """Determine the media type (audio, image, video) from the field content"""
        raw_value = self.get_raw_value(field_name)
        
        if "[sound:" in raw_value:
            return "audio"
        elif "<img" in raw_value.lower():
            return "image"
        elif "<video" in raw_value.lower():
            return "video"
        
        return None
    
    def get_media_file_path(self, field_name) -> str:
        """Get the full path to the media file from a field"""
        filename = self.extract_media_filename(field_name)
        if not filename:
            raise ValueError(f"No media filename found in field {field_name}")
        
        media_path = self.file.get_media_path(filename)
        if not media_path:
            raise ValueError(f"Media file not found for filename {filename}")
        
        return media_path
    
    def has_media(self, field_name) -> bool:
        """Check if a field contains media content"""
        try:
            filename = self.extract_media_filename(field_name)
            if not filename:
                return False
            return self.file.get_media_path(filename) is not None
        except Exception:
            return False
    
    def get_text_value(self, field_name, transform=None):
        raw_value = self.get_raw_value(field_name)
        transformed_value = transform(raw_value) if transform else raw_value
        return transformed_value
    
    def get_raw_value(self, field_name):
        if (field_name not in self.flashcard):
            available_fields = ", ".join(self.flashcard.keys())
            raise ValueError(f"Field {field_name} not found in flashcard. Available fields: {available_fields}")

        return self.flashcard[field_name]