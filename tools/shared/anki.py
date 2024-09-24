import json
import tempfile
import sqlite3
import zipfile 

# Unzips the given archive file to a temporary directory and returns the path to the directory
def extract_files(archive_file_path):
    temp_dir = tempfile.mkdtemp()
    with zipfile.ZipFile(archive_file_path, 'r') as archive:
        archive.extractall(temp_dir)
    return temp_dir

def extract_database(archive_path): 
    extracted_archive_path = extract_files(archive_path)
    database_file_path = extracted_archive_path + "\\collection.anki2"

    return sqlite3.connect(database_file_path)

def get_field_names(db): 
    # Fetch cursor 
    cursor = db.cursor()

    # Collection data is stored inside as a single row the "col" table
    collection_row = cursor.execute("SELECT * FROM col LIMIT 1").fetchone()
    
    # Collection fields are stored in the 9'th column as JSON  
    field_metadata = collection_row[9]
    field_schema = json.loads(field_metadata)

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