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
    # List all files in the folder for debugging
    try:
        files_in_folder = os.listdir(folder_path)
    except Exception as e:
        raise FileNotFoundError(f"Cannot read folder {folder_path}: {e}")
    
    # Look for collection database files (case-insensitive, multiple formats)
    # Anki uses: collection.anki21, collection.anki21b, collection.anki2
    collection_patterns = [
        "collection.anki21b",  # Newer Anki versions
        "collection.anki21",   # Anki 2.1
        "collection.anki2",    # Anki 2.0
    ]
    
    for pattern in collection_patterns:
        # Try case-sensitive match first
        exact_path = os.path.join(folder_path, pattern)
        if os.path.exists(exact_path):
            return sqlite3.connect(exact_path)
        
        # Try case-insensitive match
        for filename in files_in_folder:
            if filename.lower() == pattern.lower():
                found_path = os.path.join(folder_path, filename)
                return sqlite3.connect(found_path)
    
    # If no database found, provide helpful error message
    raise FileNotFoundError(
        f"No valid ANKI database file found in the provided folder. "
        f"Expected files: {', '.join(collection_patterns)} "
        f"Found files: {', '.join(files_in_folder)}"
    )

def fetch_media_map(folder_path):
    media_file_path = os.path.join(folder_path, "media")
    reverse_map = json.load(open(media_file_path))

    media_map = dict()
    for key, value in reverse_map.items():
        media_map[value] = os.path.join(folder_path, key)

    return media_map   

def get_model_field_map(db) -> dict[str, list[str]]:
    # Fetch model schema from Anki collection metadata
    cursor = db.cursor()
    result = cursor.execute("SELECT models FROM col LIMIT 1").fetchone()

    if not result or not result[0]:
        raise ValueError("Could not load Anki model metadata from collection database")

    field_schema = json.loads(result[0])
    model_field_map = dict()

    for model_id, model in field_schema.items():
        fields = model.get("flds", [])
        model_field_map[str(model_id)] = [field.get("name") for field in fields if field.get("name")]

    return model_field_map

def get_field_names(db): 
    model_field_map = get_model_field_map(db)
    if not model_field_map:
        return []

    # Keep default behavior: expose field names from the first model for primary mapping UI
    main_key = next(iter(model_field_map))
    return model_field_map[main_key]

# Fetches flashcards from the ANKI database 
def get_flashcards(db) -> list[dict]: 
    model_field_map = get_model_field_map(db)
    default_field_names = model_field_map[next(iter(model_field_map))] if model_field_map else []

    # Fetch flashcard fields 
    buffer = list()

    for row in db.cursor().execute("SELECT * FROM notes").fetchall():
        fields = dict()
        model_id = str(row[2]) if len(row) > 2 else None
        field_names = model_field_map.get(model_id, default_field_names)
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
        """Get field names that contain sound tags (e.g., [sound:file.mp3])."""
        import re

        buffer = list()
        sound_pattern = re.compile(r'\[sound:[^\]]+\]', re.IGNORECASE)

        candidate_field_names = list(dict.fromkeys(
            list(self.field_names or []) +
            [field for flashcard in self.flashcards for field in flashcard.flashcard.keys()]
        ))

        for field_name in candidate_field_names:
            for flashcard in self.flashcards:
                try:
                    raw_value = flashcard.get_raw_value(field_name)
                except ValueError:
                    continue
                if raw_value and sound_pattern.search(raw_value):
                    buffer.append(field_name)
                    break

        return buffer
    
    def get_image_field_names(self) -> list[str]:
        """Get field names that contain image references (e.g., <img src="file.jpg">)"""
        buffer = list()

        candidate_field_names = list(dict.fromkeys(
            list(self.field_names or []) +
            [field for flashcard in self.flashcards for field in flashcard.flashcard.keys()]
        ))

        for field_name in candidate_field_names:
            for flashcard in self.flashcards:
                try:
                    raw_value = flashcard.get_raw_value(field_name)
                except ValueError:
                    continue
                if raw_value and "<img" in raw_value.lower():
                    buffer.append(field_name)
                    break

        return buffer
    
    def get_video_field_names(self) -> list[str]:
        """Get field names that contain video references (e.g., <video src="file.mp4">)"""
        buffer = list()

        candidate_field_names = list(dict.fromkeys(
            list(self.field_names or []) +
            [field for flashcard in self.flashcards for field in flashcard.flashcard.keys()]
        ))

        for field_name in candidate_field_names:
            for flashcard in self.flashcards:
                try:
                    raw_value = flashcard.get_raw_value(field_name)
                except ValueError:
                    continue
                if raw_value and "<video" in raw_value.lower():
                    buffer.append(field_name)
                    break

        return buffer
    
    def get_media_field_names(self) -> list[str]:
        """Get all field names that contain any type of media (sound, image, or video)"""
        media_fields = list()

        for field_name in self.get_sound_field_names() + self.get_image_field_names() + self.get_video_field_names():
            if field_name not in media_fields:
                media_fields.append(field_name)

        return media_fields

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
        import re

        text_value = self.get_text_value(field_name, transform)

        sound_match = re.search(r'\[sound:([^\]]+)\]', text_value, re.IGNORECASE)
        media_name = sound_match.group(1) if sound_match else text_value.strip()

        media_path = self.file.get_media_path(media_name)
        if not media_path:
            raise ValueError(f"Media file not found for text value {media_name}")

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
        sound_match = re.search(r'\[sound:([^\]]+)\]', raw_value, re.IGNORECASE)
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
        raw_value_lower = raw_value.lower()
        
        if "[sound:" in raw_value_lower:
            return "audio"
        elif "<img" in raw_value_lower:
            return "image"
        elif "<video" in raw_value_lower:
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