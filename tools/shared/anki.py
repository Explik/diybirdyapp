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
    anki21_path = folder_path + "\\collection.anki21"
    if os.path.exists(anki21_path):
        return sqlite3.connect(anki21_path)

    # If collection.anki2 file exists, use it
    anki20_path = folder_path + "\\collection.anki2"
    if os.path.exists(anki20_path):
        return sqlite3.connect(anki20_path)

    # If neither file exists, raise an error
    raise FileNotFoundError("No valid ANKI database file found in the provided folder.")

def fetch_media_map(folder_path):
    media_file_path = folder_path + "\\media"
    reverse_map = json.load(open(media_file_path))

    media_map = dict()
    for key, value in reverse_map.items():
        media_map[value] = folder_path + "\\" + key

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

    def get_flashcards(self) -> list['AnkiCard']:
        return self.flashcards
    
    def get_number_of_flashcards(self) -> int:
        return len(self.flashcards)

    def get_media_path(self, media_name): 
        return self.media_map.get(media_name) if media_name in self.media_map else None
    
    def copy_media(self, media_name, target_folder, include_ext=True):
        source_path = self.get_media_path(media_name)
        source_file_name = source_path.split("\\")[-1]
        target_path = target_folder + "\\" + source_file_name
        
        if include_ext: 
            file_ext = media_name.split(".")[1] if "." in media_name else ""
            target_path = target_path + "." + file_ext

        shutil.copy(source_path, target_path)

        return target_path.split("\\")[-1]

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
    
    def get_text_value(self, field_name, transform=None):
        raw_value = self.get_raw_value(field_name)
        transformed_value = transform(raw_value) if transform else raw_value
        return transformed_value
    
    def get_raw_value(self, field_name):
        if (field_name not in self.flashcard):
            available_fields = ", ".join(self.flashcard.keys())
            raise ValueError(f"Field {field_name} not found in flashcard. Available fields: {available_fields}")

        return self.flashcard[field_name]