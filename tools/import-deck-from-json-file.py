from shared.diybirdyapp import get_language_by_name, create_flashcard_deck, create_text_flashcard, get_language_by_abbrevation
import json
import sys

## Imports JSON files in the following format: 
# {
#     "name": "Flashcard deck name",
#     "description": "Flashcard deck description",
#     "flashcards": [
#         {
#             "frontContent": {
#                 "text": "Text on front of card 1",
#                 "language": { "abbreviation": "en" }
#             },
#             "backContent": {
#                 "text": "Text on back of card 1",
#                 "language": { "abbreviation": "en" }
#             }
#         }
#     ]
# }

def get_language_from_object(obj): 
    if ('name' in obj):
        return get_language_by_name(obj["name"])
    if ('abbrivation' in obj):
        return get_language_by_abbrevation(obj["abbrivation"])
    else: 
        raise ValueError("No language found in content", obj)

def read_deck_from_file(file_name): 
    name = None
    description = None
    flashcards = None

    with open(file_name, 'r') as file:
        data = json.load(file)
        name = data["name"]
        description = data["description"] if "description" in data else None
        flashcards = data["flashcards"]

    if not name: 
        raise ValueError("No deck name found in data.json")
    if not flashcards or len(flashcards) == 0:
        raise ValueError("No flashcards found in data.json")

    for flashcard in flashcards:
        if not flashcard["frontContent"]:
            raise ValueError("No front content found in flashcard")
        if not flashcard["backContent"]:
            raise ValueError("No back content found in flashcard")
        
        if flashcard["frontContent"]["type"] != "text" or flashcard["backContent"]["type"] != "text":
            raise ValueError("Only text flashcards are supported")
        
    return {
        "name": name,
        "description": description,
        "flashcards": flashcards
    }

# Import flashcards
if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python __main__.py <data.json>")
        sys.exit(1)

    file_name = sys.argv[1]
    file_data = read_deck_from_file(file_name)

    deck = create_flashcard_deck(file_data["name"], file_data["description"])

    for index, flashcard in enumerate(file_data["flashcards"]): 
        # Fetch content 
        frontTextContent = flashcard["frontContent"]["text"]
        backTextContent = flashcard["backContent"]["text"]
        
        # Resolve language
        frontLanguage = get_language_from_object(flashcard["frontContent"]["language"])
        backLanguage = get_language_from_object(flashcard["backContent"]["language"])

        # Create flashcard
        flashcard_copy = json.loads(json.dumps(flashcard))

        create_text_flashcard(
            deck, 
            index + 1,
            frontLanguage, 
            backLanguage,
            frontTextContent, 
            backTextContent)