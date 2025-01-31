
from uuid import UUID
from json import dumps
from shared.anki import extract_database, get_field_names, get_flashcards
from shared.diybirdyapp import create_flashcard_deck, create_text_flashcard, get_language_by_name

input_file = "[INPUT_FILE_PATH]"
output_file = "[OUTPUT_FILE_PATH]"
name = "[DECK_NAME]"
description = "[DECK_DESCRIPTION]"
field_name1 = "[TOP_FIELD_NAME]"
field_name2 = "[BOTTOM_FIELD_NAME]"
field_language1 = "[TOP_FIELD_LANGUAGE]"
field_language2 = "[BOTTOM_FIELD_LANGUAGE]"

# Extract data from ANKI2 DB 
anki_db = extract_database(input_file)
field_names = get_field_names(anki_db)
flashcards = get_flashcards(anki_db)

# Validate field names
if field_name1 not in field_names: 
    raise ValueError(f"Field {field_name1} not found in ANKI database")

if field_name2 not in field_names:
    raise ValueError(f"Field {field_name2} not found in ANKI database")

# Fetch languages 
language1 = get_language_by_name(field_language1)
language2 = get_language_by_name(field_language2)

# Creates flashcard deck
flashcard_deck = create_flashcard_deck(name, description)

# Creates flashcards
for index, flashcard in enumerate(flashcards): 
    field1_value = flashcard[field_name1]
    field2_value = flashcard[field_name2]

    create_text_flashcard(
        flashcard_deck, 
        index + 1,
        language1, 
        language2,
        field1_value, 
        field2_value)