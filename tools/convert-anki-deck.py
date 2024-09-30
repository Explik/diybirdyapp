
from uuid import UUID
from json import dumps
from shared.anki import extract_database, get_field_names, get_flashcards

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

# Converts flashcard deck to internal JSON representation
# {
#   "name": string, 
#   "description": string,
#   "flashcards": [
#     {
#       "leftValue": string,
#       "leftLanguage": { "id": UUID, "name": string, "abbreviation": string },
#       "rightValue": string,
#       "rightLanguage": { "id": UUID, "name": string, "abbreviation": string }
#     }
# }
converted_flashcards = []
for flashcard in flashcards:
    left_value = flashcard[field_name1]
    right_value = flashcard[field_name2]

    converted_flashcards.append({
        "leftValue": left_value,
        "leftLanguage": { "abbreviation": field_language1 },
        "rightValue": right_value,
        "rightLanguage": { "abbreviation": field_language2 }
    })

converted_deck = {
    "name": "English - Slovak A1 - Beginners",
    "description": "Flashcards for English - Slovak A1 - Beginners",
    "flashcards": converted_flashcards
} 

# Commit converted deck to JSON file
with open(output_file, "w") as file:
    json_object = dumps(converted_deck, indent=4)
    file.write(json_object)