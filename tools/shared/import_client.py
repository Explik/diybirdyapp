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
from .deck_storage import DeckStorage

# Setup the API client
config = Configuration()
config.host = "http://localhost:8080"
client = ApiClient(configuration=config)

# Create an API instance
language_api = LanguageControllerApi(client)
flashcard_deck_controller = FlashcardDeckControllerApi(client)
flashcard_api = FlashcardControllerApi(client)

# Create deck storage instance
deck_storage = DeckStorage()

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


# Local storage functions
def create_local_flashcard_deck(name, description=""):
    """
    Create a flashcard deck locally without uploading to the server.
    
    Args:
        name: Name of the deck
        description: Description of the deck
        
    Returns:
        Dictionary containing deck metadata and local paths
    """
    deck_metadata = deck_storage.create_deck(name, description)
    print(f"Created local deck: {name} at {deck_metadata['deck_dir']}")
    return deck_metadata


def add_local_text_flashcard(deck_metadata, deck_order, front_language, back_language, front_text, back_text):
    """
    Add a text flashcard to a local deck.
    
    Args:
        deck_metadata: Dictionary returned by create_local_flashcard_deck
        deck_order: Order of the flashcard in the deck
        front_language: Language object for the front text
        back_language: Language object for the back text
        front_text: Text for the front of the card
        back_text: Text for the back of the card
        
    Returns:
        The created flashcard dictionary
    """
    front_content = {
        "type": "text",
        "text": front_text,
        "languageId": front_language,
    }
    
    back_content = {
        "type": "text",
        "text": back_text,
        "languageId": back_language,
    }
    
    flashcard = deck_storage.add_flashcard(
        deck_dir=deck_metadata['deck_dir'],
        deck_order=deck_order,
        front_content=front_content,
        back_content=back_content
    )
    
    print(f"Added local flashcard: {front_text} | {back_text}")
    return flashcard


def add_local_pronunciation(deck_metadata, flashcard_id, side, audio_file):
    """
    Add pronunciation audio to a flashcard in a local deck.
    
    Args:
        deck_metadata: Dictionary returned by create_local_flashcard_deck
        flashcard_id: ID of the flashcard
        side: "front" or "back"
        audio_file: Path to the audio file
        
    Returns:
        The relative path to the media file
    """
    media_path = deck_storage.add_pronunciation(
        deck_dir=deck_metadata['deck_dir'],
        flashcard_id=flashcard_id,
        side=side,
        audio_file=audio_file
    )
    
    print(f"Added pronunciation to {flashcard_id} ({side}): {media_path}")
    return media_path


def get_local_deck_data(deck_metadata):
    """
    Get the data from a local deck.
    
    Args:
        deck_metadata: Dictionary returned by create_local_flashcard_deck
        
    Returns:
        Dictionary containing the deck data
    """
    return deck_storage.get_deck_data(deck_metadata['deck_dir'])


def create_deck_zip(deck_metadata, output_path=None):
    """
    Create a ZIP file from a local deck.
    
    Args:
        deck_metadata: Dictionary returned by create_local_flashcard_deck
        output_path: Optional path for the output ZIP file
        
    Returns:
        Path to the created ZIP file
    """
    zip_path = deck_storage.create_zip(deck_metadata['deck_dir'], output_path)
    print(f"Created ZIP file: {zip_path}")
    return zip_path


def list_local_decks():
    """
    List all local decks.
    
    Returns:
        List of deck metadata dictionaries
    """
    return deck_storage.list_decks()


def upload_local_deck(deck_metadata):
    """
    Upload a local deck to the server.
    
    Args:
        deck_metadata: Dictionary returned by create_local_flashcard_deck
        
    Returns:
        The created deck object from the server
    """
    # Get local deck data
    deck_data = deck_storage.get_deck_data(deck_metadata['deck_dir'])
    
    # Create deck on server
    server_deck = create_flashcard_deck(deck_data['name'], deck_data.get('description', ''))
    
    # Upload flashcards
    for flashcard in deck_data['flashcards']:
        # Extract front and back content
        front_content = flashcard['frontContent']
        back_content = flashcard['backContent']
        
        # Get language objects
        front_lang = get_language_by_abbrevation(front_content['languageAbbreviation'])
        back_lang = get_language_by_abbrevation(back_content['languageAbbreviation'])
        
        # Create flashcard on server
        if front_content['type'] == 'text' and back_content['type'] == 'text':
            create_text_flashcard(
                deck=server_deck,
                deck_order=flashcard['deckOrder'],
                front_language=front_lang,
                back_language=back_lang,
                front_text=front_content['text'],
                back_text=back_content['text']
            )
    
    print(f"Uploaded deck: {deck_data['name']} with {len(deck_data['flashcards'])} flashcards")
    return server_deck
