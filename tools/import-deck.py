from shared.import_client import get_language_by_name, create_flashcard_deck, create_text_flashcard, get_language_by_abbrevation, create_flashcard
import json
import sys

## Imports [data].json in the following format: 
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

def read_data_file(file_path): 
    data = None

    with open(file_path, 'r') as file:
        data = json.load(file)

    if not data["name"]: 
        raise ValueError("No deck name found in data.json")
    if not data["flashcards"] or len(data["flashcards"]) == 0:
        raise ValueError("No flashcards found in data.json")

    for flashcard in data["flashcards"]:
        if not flashcard["frontContent"]:
            raise ValueError("No front content found in flashcard")
        if not flashcard["backContent"]:
            raise ValueError("No back content found in flashcard")

    return data

def get_language_from_query(obj): 
    if ('name' in obj):
        return get_language_by_name(obj["name"])
    if ('abbrivation' in obj):
        return get_language_by_abbrevation(obj["abbrivation"])
    else: 
        raise ValueError("No language found in content", obj)

def get_files_from_flashcard_content(folder, content): 
    if (content["type"] == "audio-upload"):
        return [folder + "\\" + content["audioFileName"]]
    if (content["type"] == "image-upload"):
        return [folder + "\\" + content["imageFileName"]]
    if (content["type"] == "video-upload"):
        return [folder + "\\" + content["videoFileName"]]
    
    return []

# Import flashcards
if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python __main__.py <data.json>")
        sys.exit(1)

    file_name = sys.argv[1]
    file_folder = file_name.rsplit("\\", 1)[0]
    file_data = read_data_file(file_name)

    deck = create_flashcard_deck(file_data["name"], file_data["description"])

    for index, flashcard in enumerate(file_data["flashcards"]): 
        # Create flashcard payload (and replace language query with languageId)
        flashcard_copy = json.loads(json.dumps(flashcard))
        flashcard_copy["deckOrder"] = index + 1

        if "language" in flashcard_copy["frontContent"]:
            front_language = get_language_from_query(flashcard["frontContent"]["language"])
            del flashcard_copy["frontContent"]["language"]
            flashcard_copy["frontContent"]["languageId"] = front_language.id

        if "language" in flashcard_copy["backContent"]:
            back_language = get_language_from_query(flashcard["backContent"]["language"])
            del flashcard_copy["backContent"]["language"]
            flashcard_copy["backContent"]["languageId"] = back_language.id

        # Extract list of flashcard files
        file_paths = []
        file_paths.extend(get_files_from_flashcard_content(file_folder, flashcard_copy["frontContent"]))
        file_paths.extend(get_files_from_flashcard_content(file_folder, flashcard_copy["backContent"]))

        # Send flashcard to server
        create_flashcard(deck, flashcard_copy, file_paths)