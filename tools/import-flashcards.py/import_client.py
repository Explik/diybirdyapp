import json
from shared.api_client.openapi_client.models.flashcard_content_text_dto import FlashcardContentTextDto
from shared.api_client.openapi_client import Configuration
from shared.api_client.openapi_client.api_client import ApiClient
from shared.api_client.openapi_client.api.flashcard_controller_api import FlashcardControllerApi
from shared.api_client.openapi_client.api.language_controller_api import LanguageControllerApi
from shared.api_client.openapi_client.api.configuration_controller_api import ConfigurationControllerApi
from shared.api_client.openapi_client.api.flashcard_deck_controller_api import FlashcardDeckControllerApi
from shared.api_client.openapi_client.models.flashcard_dto import FlashcardDto
import random
import requests
from deck_storage import DeckStorage
from shared.auth import get_session_cookie

# Setup the API client
config = Configuration()
config.host = "http://localhost:8080"
client = ApiClient(configuration=config)

# Create an API instance
language_api = LanguageControllerApi(client)
configuration_api = ConfigurationControllerApi(client)
flashcard_deck_controller = FlashcardDeckControllerApi(client)
flashcard_api = FlashcardControllerApi(client)

def update_api_client_auth():
    """Update the API client with the current session cookie for authentication"""
    session_cookie = get_session_cookie()
    if session_cookie:
        # Set Cookie header with JSESSIONID for authenticated requests
        client.set_default_header('Cookie', f'JSESSIONID={session_cookie}')
    else:
        # Clear the Cookie header if no session
        if 'Cookie' in client.default_headers:
            del client.default_headers['Cookie']

# Create deck storage instance
deck_storage = DeckStorage()

def _get_request_cookies():
    """Helper function to get cookies with session ID for authenticated requests"""
    session_cookie = get_session_cookie()
    if session_cookie:
        return {'JSESSIONID': session_cookie}
    return {}

# Define functions 
def get_languages(): 
    # Fetch languages
    update_api_client_auth()  # Ensure API client has current session cookie
    languages = language_api.get_all()
    return languages

def get_language_by_abbrevation(abbreviation):
    # Get a language by abbrievation
    update_api_client_auth()  # Ensure API client has current session cookie
    languages = language_api.get_all()
    language = next((lang for lang in languages if lang.abbreviation == abbreviation), None)

    if not language:
        raise ValueError(f"Language with abbreviation {abbreviation} not found")

    return language

def get_language_by_name(name):
    # Get a language by name
    update_api_client_auth()  # Ensure API client has current session cookie
    languages = language_api.get_all()
    language = next((lang for lang in languages if lang.name == name), None)

    if not language:
        raise ValueError(f"Language with name {name} not found")


    return language

def get_configurations(language_id):
    """
    Get all configurations for a specific language.
    
    Args:
        language_id: The ID of the language
        
    Returns:
        List of configuration objects
    """
    update_api_client_auth()  # Ensure API client has current session cookie
    configurations = configuration_api.get_all3(language_id=language_id)
    return configurations

def get_language_code(language_id):
    """
    Get the language code for a specific language.
    Uses the first configuration found for the language.
    
    Args:
        language_id: The ID of the language
        
    Returns:
        The language code (e.g., "en-US")
    """
    configurations = get_configurations(language_id)
    if not configurations:
        raise ValueError(f"No configuration found for language {language_id}")
    
    # Use the first configuration
    return configurations[0].language_code

def create_flashcard_deck(name, description):
    # Create a flashcard deck
    update_api_client_auth()  # Ensure API client has current session cookie
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

    response = requests.post(
        "http://localhost:8080/flashcard/rich", 
        files=form_data,
        cookies=_get_request_cookies()
    )

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

    response = requests.post(
        "http://localhost:8080/flashcard", 
        json=flashcard,
        cookies=_get_request_cookies()
    )

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


def get_local_deck_data(deck_name_or_metadata):
    """
    Get the data from a local deck.
    
    Args:
        deck_name_or_metadata: Either a deck name (string) or a deck metadata dictionary
                              containing 'deck_dir'
        
    Returns:
        Dictionary containing the deck metadata with data included
    """
    if isinstance(deck_name_or_metadata, str):
        # It's a deck name, find it in the list
        decks = deck_storage.list_decks()
        deck = next((d for d in decks if d['name'] == deck_name_or_metadata), None)
        if not deck:
            raise ValueError(f"Deck '{deck_name_or_metadata}' not found")
        return deck
    else:
        # It's a metadata dict, just get the data
        deck_dir = deck_name_or_metadata.get('deck_dir')
        if not deck_dir:
            raise ValueError("deck_metadata must contain 'deck_dir' key")
        
        data = deck_storage.get_deck_data(deck_dir)
        return {
            **deck_name_or_metadata,
            'data': data
        }


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
    
    This function follows the two-step upload process documented in the README:
    1. Create flashcard deck on server using POST /flashcard-deck/ (FlashcardDeckDto)
    2. Upload flashcards:
       - Text-only flashcards via POST /flashcard (FlashcardDto)
       - Rich media flashcards via POST /flashcard/rich (form with FlashcardDto + media files)
    
    The internal deck format includes:
    - data.json: Contains flashcard data with languageId references
    - media/: Directory containing media files (audio, images, etc.)
    - Pronunciation: Stored in content.pronunciation.content as relative path to media file
    
    Args:
        deck_metadata: Dictionary returned by create_local_flashcard_deck or 
                      from list_local_decks() containing at minimum:
                      - 'name': deck name
                      - 'deck_dir': path to the deck directory
                      - 'data': (optional) deck data to avoid re-reading
        
    Returns:
        The created deck object from the server
    """
    import os
    
    # Handle both metadata formats
    if isinstance(deck_metadata, str):
        # If just a string path, convert to dict
        deck_dir = deck_metadata
        deck_data = deck_storage.get_deck_data(deck_dir)
    else:
        deck_dir = deck_metadata.get('deck_dir', deck_metadata)
        # Use pre-loaded data if available
        if 'data' in deck_metadata:
            deck_data = deck_metadata['data']
        else:
            deck_data = deck_storage.get_deck_data(deck_dir)
    
    # Step 1: Create deck on server using POST /flashcard-deck/
    server_deck = create_flashcard_deck(deck_data['name'], deck_data.get('description', ''))
    print(f"Created deck on server: {server_deck.name} (ID: {server_deck.id})")
    
    # Step 2: Upload flashcards
    for flashcard in deck_data['flashcards']:
        # Extract front and back content
        front_content = flashcard['frontContent']
        back_content = flashcard['backContent']
        
        # Determine if this flashcard has media files (pronunciation, images, audio, etc.)
        has_media = False
        media_files = []
        
        # Check for pronunciation audio in front content
        if 'pronunciation' in front_content and 'content' in front_content['pronunciation']:
            has_media = True
            media_path = os.path.join(deck_dir, front_content['pronunciation']['content'])
            if os.path.exists(media_path):
                media_files.append(media_path)
            else:
                print(f"Warning: Pronunciation file not found: {media_path}")
        
        # Check for pronunciation audio in back content
        if 'pronunciation' in back_content and 'content' in back_content['pronunciation']:
            has_media = True
            media_path = os.path.join(deck_dir, back_content['pronunciation']['content'])
            if os.path.exists(media_path):
                media_files.append(media_path)
            else:
                print(f"Warning: Pronunciation file not found: {media_path}")
        
        # Check if content type is non-text (image, audio, video)
        front_type = front_content.get('type', 'text')
        back_type = back_content.get('type', 'text')
        
        if front_type in ['image', 'audio', 'video']:
            has_media = True
            if 'content' in front_content:
                media_path = os.path.join(deck_dir, front_content['content'])
                if os.path.exists(media_path):
                    media_files.append(media_path)
                else:
                    print(f"Warning: Media file not found: {media_path}")
        
        if back_type in ['image', 'audio', 'video']:
            has_media = True
            if 'content' in back_content:
                media_path = os.path.join(deck_dir, back_content['content'])
                if os.path.exists(media_path):
                    media_files.append(media_path)
                else:
                    print(f"Warning: Media file not found: {media_path}")
        
        # Build flashcard DTO matching the FlashcardDto structure
        flashcard_dto = {
            "id": flashcard.get('id', str(random.randint(1, 1000000))),
            "deckId": server_deck.id,
            "deckOrder": flashcard['deckOrder'],
            "frontContent": {
                "type": front_type,
                "languageId": front_content['languageId']
            },
            "backContent": {
                "type": back_type,
                "languageId": back_content['languageId']
            }
        }
        
        # Add text content for text-type cards
        if 'text' in front_content:
            flashcard_dto['frontContent']['text'] = front_content['text']
        if 'text' in back_content:
            flashcard_dto['backContent']['text'] = back_content['text']
        
        # Add content field for non-text types (relative path to media file)
        if 'content' in front_content and front_type != 'text':
            flashcard_dto['frontContent']['content'] = front_content['content']
        if 'content' in back_content and back_type != 'text':
            flashcard_dto['backContent']['content'] = back_content['content']
        
        # Choose upload method based on whether we have media files
        if has_media and media_files:
            # Use POST /flashcard/rich for flashcards with media files
            # This includes pronunciation audio, images, videos, etc.
            create_flashcard(server_deck, flashcard_dto, media_files)
            print(f"Uploaded rich flashcard {flashcard_dto['id']} with {len(media_files)} media file(s)")
        else:
            # Use POST /flashcard for simple text-only flashcards
            if front_type == 'text' and back_type == 'text':
                response = requests.post(
                    "http://localhost:8080/flashcard", 
                    json=flashcard_dto,
                    cookies=_get_request_cookies()
                )
                
                if response.status_code != 200:
                    raise Exception(f"Failed to create flashcard: {response.text}")
                
                flashcard_response = response.json()
                print(f"Uploaded text flashcard: {front_content.get('text', '')} | {back_content.get('text', '')}")
            else:
                # Fallback to rich endpoint even without media files for non-text types
                create_flashcard(server_deck, flashcard_dto, [])
                print(f"Uploaded rich flashcard {flashcard_dto['id']} (no media files)")
    
    print(f"✅ Successfully uploaded deck '{deck_data['name']}' with {len(deck_data['flashcards'])} flashcards")
    return server_deck
