import json
from .api_client.openapi_client.models.flashcard_content_text_dto import FlashcardContentTextDto
from .api_client.openapi_client import Configuration
from .api_client.openapi_client.api_client import ApiClient
from .api_client.openapi_client.api.flashcard_controller_api import FlashcardControllerApi
from .api_client.openapi_client.api.language_controller_api import LanguageControllerApi
from .api_client.openapi_client.api.flashcard_deck_controller_api import FlashcardDeckControllerApi
from .api_client.openapi_client.models.flashcard_dto import FlashcardDto
import random
import requests

# Setup the API client
config = Configuration()
config.host = "http://localhost:8080"
client = ApiClient(configuration=config)

# Create an API instance
language_api = LanguageControllerApi(client)
flashcard_deck_controller = FlashcardDeckControllerApi(client)
flashcard_api = FlashcardControllerApi(client)

# Define functions 
def get_languages(): 
    # Fetch languages
    languages = language_api.get_all()
    return languages

def get_language_by_abbrevation(abbreviation):
    # Get a language by abbrievation
    languages = language_api.get_all()
    language = next((lang for lang in languages if lang.abbreviation == abbreviation), None)

    if not language:
        raise ValueError(f"Language with abbreviation {abbreviation} not found")

    return language

def get_language_by_name(name):
    # Get a language by name
    languages = language_api.get_all()
    language = next((lang for lang in languages if lang.name == name), None)

    if not language:
        raise ValueError(f"Language with name {name} not found")

    return language

def create_flashcard_deck(name, description):
    # Create a flashcard deck
    random_id = str(random.randint(1, 1000000))
    deck = flashcard_deck_controller.create1({ "id": random_id, "name": name, "description": description })
    
    print("Created deck: ", deck)

    return deck

def create_flashcard(deck, flashcard, file_paths = None):
    # Manually create a FlashcardContentTextDto object as OpenAPI classes fail on serialization
    if "id" not in flashcard: 
        flashcard["id"] = str(random.randint(1, 1000000))
    if "deckId" not in flashcard:
        flashcard["deckId"] = deck.id

    form_data = [
        ("flashcard", (None, json.dumps(flashcard), "application/json"))
    ]
    for file_path in file_paths or []:
        file_name = file_path.split("\\")[-1] 
        form_data.append(("files", (file_name, open(file_path, "rb"), "application/octet-stream")))

    response = requests.post("http://localhost:8080/flashcard/rich", files=form_data)

    if response.status_code != 200:
        raise Exception(f"Failed to create flashcard: {response.text}")
    
    flashcard_response = response.json()
    print("Created flashcard: ", flashcard_response)

    return flashcard_response

def create_text_flashcard(deck, deck_order, front_language, back_language, front_text, back_text):
    id = str(random.randint(1, 1000000))
    deck_id = deck.id
    front_language_id = front_language.id
    back_language_id = back_language.id

    # Manually create a FlashcardContentTextDto object as OpenAPI classes fail on serialization
    flashcard = {
        "id": id,
        "deckId": deck_id,
        "deckOrder": deck_order,
        "frontContent": {
            "type": "text",
            "text": front_text,
            "languageId": front_language_id
            
        },
        "backContent": {
            "type": "text",
            "text": back_text,
            "languageId": back_language_id
        }
    }

    response = requests.post("http://localhost:8080/flashcard", json=flashcard)

    if response.status_code != 200:
        raise Exception(f"Failed to create flashcard: {response.text}")

    flashcard_response = response.json()
    print("Created flashcard: ", front_text, back_text)

    return flashcard_response