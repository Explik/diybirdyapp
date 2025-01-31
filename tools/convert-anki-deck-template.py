
################################################################################
# This is a template for converting an .anki file to an importable folder. 
# The folder can be imported using the import-deck-from-folder.py script. 
# 
# The template is meant to be used as a starting point for creating a custom conversion for a specific .anki file.
################################################################################
import json
import os
from shared.anki import AnkiDeck
from shared.utils import strip_prefix_suffix
from shared.converter_utils import create_audio_content_upload, create_flashcard, create_flashcard_deck, create_language_query, create_text_content

# Load data from ANKI file
file_path = "[INPUT ANKI FILE PATH]"
file = AnkiDeck.create_from_file(file_path)

# Create output folder 
output_folder = "[OUTPUT FOLDER PATH]"
os.makedirs(output_folder, exist_ok=True)

# Extract flashcards
flashcards = []

front_language = create_language_query(name = "English")
back_language = create_language_query(name = "Slovak")

for flashcard in file.get_flashcards(): 
    try: 
        # Create text-text flashcard
        front_text = flashcard.get_text_value("Word1")
        back_text = flashcard.get_text_value("Word2")

        text_flashcard = create_flashcard(
            create_text_content(front_text, front_language), 
            create_text_content(back_text, back_language))
        flashcards.append(text_flashcard)
        
        # Create text-audio flashcards
        pronounciation_file_name = flashcard.get_text_value("Sound2", strip_prefix_suffix("[sound:", "]"))
        pronounciation_file_path = file.copy_media(pronounciation_file_name, output_folder) 

        pronounciation_flashcard = create_flashcard(
            create_text_content(front_text, front_language), 
            create_audio_content_upload(pronounciation_file_path, back_language))
        flashcards.append(pronounciation_flashcard)
       
    except Exception as e:
        print("Failed to create flashcard: ", flashcard, e)

# Save flashcards to file
flashcard_deck = create_flashcard_deck(
    "English - Slovak A", 
    "A deck for learning English to Slovak", 
    flashcards)

output_file = output_folder + "\\data.json"
with open(output_file, 'w') as file:
    json.dump(flashcard_deck, file, indent=4)