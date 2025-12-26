import json
import os
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
import streamlit as st

# Setup the API client
config = Configuration()
config.host = "http://localhost:8080"
client = ApiClient(configuration=config)

def get_backend_url():
    """Get the backend URL from session state or use default"""
    url = st.session_state.get('backend_url', 'http://localhost:8080')
    # Strip whitespace and ensure no trailing slash
    return url.strip().rstrip('/')

# Create an API instance
language_api = LanguageControllerApi(client)
configuration_api = ConfigurationControllerApi(client)
flashcard_deck_controller = FlashcardDeckControllerApi(client)
flashcard_api = FlashcardControllerApi(client)

def update_api_client_auth():
    """Update the API client with the current session cookie for authentication"""
    # Update host from session state
    backend_url = get_backend_url()
    config.host = backend_url
    
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
    # Get a language by isoCode
    update_api_client_auth()  # Ensure API client has current session cookie
    languages = language_api.get_all()
    language = next((lang for lang in languages if lang.iso_code == abbreviation), None)

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

def get_configurations(language_id, type=None):
    """
    Get all configurations for a specific language.
    
    Args:
        language_id: The ID of the language
        
    Returns:
        List of configuration objects
    """
    update_api_client_auth()  # Ensure API client has current session cookie
    configurations = language_api.get_configs(language_id, type)
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
    configurations = get_configurations(language_id, "google-translate")
    if not configurations:
        raise ValueError(f"No configuration found for language {language_id}")
    
    # Use the first configuration
    return configurations[0].language_code

def translate_text(source_language_id, target_language_id, text):
    """
    Translate text using the backend translation API.
    
    Args:
        source_language_id: The ID of the source language (or None for auto-detect)
        target_language_id: The ID of the target language
        text: The text to translate
        
    Returns:
        The translated text
    """
    backend_url = get_backend_url()
    
    payload = {
        "sourceLanguageId": source_language_id,
        "targetLanguageId": target_language_id,
        "text": text
    }
    
    response = requests.post(
        f"{backend_url}/translation/translate",
        json=payload,
        cookies=_get_request_cookies()
    )
    
    if response.status_code != 200:
        raise Exception(f"Failed to translate text: {response.text}")
    
    result = response.json()
    return result.get("translatedText", "")

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

    backend_url = get_backend_url()
    response = requests.post(
        f"{backend_url}/flashcard/rich", 
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

    backend_url = get_backend_url()
    response = requests.post(
        f"{backend_url}/flashcard", 
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
        
        # Determine if this flashcard has media files (images, audio, video - NOT pronunciation)
        # Pronunciation will be uploaded separately via /text-content/{id}/upload-pronunciation
        has_media = False
        media_files = []
        
        # Check if content type is non-text (image, audio, video)
        front_type = front_content.get('type', 'text')
        back_type = back_content.get('type', 'text')
        
        # Helper function to get media file path from content
        def get_media_file_from_content(content, content_type):
            """Extract media file path from content based on type"""
            if content_type == 'audio-upload' and 'audioFileName' in content:
                return os.path.join(deck_dir, 'media', content['audioFileName'])
            elif content_type == 'image-upload' and 'imageFileName' in content:
                return os.path.join(deck_dir, 'media', content['imageFileName'])
            elif content_type == 'video-upload' and 'videoFileName' in content:
                return os.path.join(deck_dir, 'media', content['videoFileName'])
            elif content_type in ['image', 'audio', 'video'] and 'content' in content:
                # Legacy format support
                return os.path.join(deck_dir, content['content'])
            return None
        
        # Check front content for media
        if front_type in ['image', 'audio', 'video', 'audio-upload', 'image-upload', 'video-upload']:
            media_path = get_media_file_from_content(front_content, front_type)
            if media_path and os.path.exists(media_path):
                has_media = True
                media_files.append(media_path)
            elif media_path:
                print(f"Warning: Media file not found: {media_path}")
        
        # Check back content for media
        if back_type in ['image', 'audio', 'video', 'audio-upload', 'image-upload', 'video-upload']:
            media_path = get_media_file_from_content(back_content, back_type)
            if media_path and os.path.exists(media_path):
                has_media = True
                media_files.append(media_path)
            elif media_path:
                print(f"Warning: Media file not found: {media_path}")
        
        # Build flashcard DTO matching the FlashcardDto structure
        flashcard_dto = {
            "id": flashcard.get('id', str(random.randint(1, 1000000))),
            "deckId": server_deck.id,
            "deckOrder": flashcard['deckOrder'],
            "frontContent": {
                "type": front_type,
            },
            "backContent": {
                "type": back_type,
            }
        }
        
        # Add languageId where applicable
        if 'languageId' in front_content:
            flashcard_dto['frontContent']['languageId'] = front_content['languageId']
        if 'languageId' in back_content:
            flashcard_dto['backContent']['languageId'] = back_content['languageId']
        
        # Add text content for text-type cards
        if 'text' in front_content:
            flashcard_dto['frontContent']['text'] = front_content['text']
        if 'text' in back_content:
            flashcard_dto['backContent']['text'] = back_content['text']
        
        # Add filename fields for upload types
        if front_type == 'audio-upload' and 'audioFileName' in front_content:
            flashcard_dto['frontContent']['audioFileName'] = front_content['audioFileName']
        elif front_type == 'image-upload' and 'imageFileName' in front_content:
            flashcard_dto['frontContent']['imageFileName'] = front_content['imageFileName']
        elif front_type == 'video-upload' and 'videoFileName' in front_content:
            flashcard_dto['frontContent']['videoFileName'] = front_content['videoFileName']
        
        if back_type == 'audio-upload' and 'audioFileName' in back_content:
            flashcard_dto['backContent']['audioFileName'] = back_content['audioFileName']
        elif back_type == 'image-upload' and 'imageFileName' in back_content:
            flashcard_dto['backContent']['imageFileName'] = back_content['imageFileName']
        elif back_type == 'video-upload' and 'videoFileName' in back_content:
            flashcard_dto['backContent']['videoFileName'] = back_content['videoFileName']
        
        # Add content field for legacy non-text types (relative path to media file)
        if 'content' in front_content and front_type in ['audio', 'video', 'image']:
            flashcard_dto['frontContent']['content'] = front_content['content']
        if 'content' in back_content and back_type in ['audio', 'video', 'image']:
            flashcard_dto['backContent']['content'] = back_content['content']
        
        # Choose upload method based on whether we have media files
        if has_media and media_files:
            # Use POST /flashcard/rich for flashcards with media files
            # This includes audio, images, videos, etc.
            server_flashcard = create_flashcard(server_deck, flashcard_dto, media_files)
            print(f"Uploaded rich flashcard {flashcard_dto['id']} with {len(media_files)} media file(s)")
        else:
            # Use POST /flashcard for simple text-only flashcards
            if front_type == 'text' and back_type == 'text':
                backend_url = get_backend_url()
                response = requests.post(
                    f"{backend_url}/flashcard", 
                    json=flashcard_dto,
                    cookies=_get_request_cookies()
                )
                
                if response.status_code != 200:
                    raise Exception(f"Failed to create flashcard: {response.text}")
                
                server_flashcard = response.json()
                print(f"Uploaded text flashcard: {front_content.get('text', '')} | {back_content.get('text', '')}")
            else:
                # Fallback to rich endpoint even without media files for non-text types
                # This handles cases where media files might be missing
                server_flashcard = create_flashcard(server_deck, flashcard_dto, [])
                print(f"Uploaded rich flashcard {flashcard_dto['id']} (no media files found)")
        
        # Upload pronunciation audio separately using /text-content/{id}/upload-pronunciation
        # Check front content for pronunciation
        if 'pronunciation' in front_content and 'content' in front_content['pronunciation']:
            pronunciation_path = os.path.join(deck_dir, front_content['pronunciation']['content'])
            if os.path.exists(pronunciation_path):
                # Get the text content ID from the server response
                front_content_id = server_flashcard.get('frontContent', {}).get('id')
                if front_content_id:
                    _upload_pronunciation(front_content_id, pronunciation_path)
                    print(f"  Uploaded front pronunciation for flashcard {flashcard_dto['id']}")
                else:
                    print(f"Warning: No front content ID found in server response for flashcard {flashcard_dto['id']}")
            else:
                print(f"Warning: Pronunciation file not found: {pronunciation_path}")
        
        # Upload transcription separately using /text-content/{id}/add-transcription
        # Check front content for transcription
        if 'transcription' in front_content:
            front_content_id = server_flashcard.get('frontContent', {}).get('id')
            if front_content_id:
                _upload_transcription(
                    front_content_id, 
                    front_content['transcription'].get('transcription'),
                    front_content['transcription'].get('transcriptionSystem')
                )
                print(f"  Uploaded front transcription for flashcard {flashcard_dto['id']}")
            else:
                print(f"Warning: No front content ID found in server response for flashcard {flashcard_dto['id']}")
        
        # Check back content for pronunciation
        if 'pronunciation' in back_content and 'content' in back_content['pronunciation']:
            pronunciation_path = os.path.join(deck_dir, back_content['pronunciation']['content'])
            if os.path.exists(pronunciation_path):
                # Get the text content ID from the server response
                back_content_id = server_flashcard.get('backContent', {}).get('id')
                if back_content_id:
                    _upload_pronunciation(back_content_id, pronunciation_path)
                    print(f"  Uploaded back pronunciation for flashcard {flashcard_dto['id']}")
                else:
                    print(f"Warning: No back content ID found in server response for flashcard {flashcard_dto['id']}")
            else:
                print(f"Warning: Pronunciation file not found: {pronunciation_path}")
        
        # Check back content for transcription
        if 'transcription' in back_content:
            back_content_id = server_flashcard.get('backContent', {}).get('id')
            if back_content_id:
                _upload_transcription(
                    back_content_id,
                    back_content['transcription'].get('transcription'),
                    back_content['transcription'].get('transcriptionSystem')
                )
                print(f"  Uploaded back transcription for flashcard {flashcard_dto['id']}")
            else:
                print(f"Warning: No back content ID found in server response for flashcard {flashcard_dto['id']}")
    
    print(f"✅ Successfully uploaded deck '{deck_data['name']}' with {len(deck_data['flashcards'])} flashcards")
    return server_deck


def _upload_pronunciation(text_content_id, audio_file_path):
    """
    Helper function to upload pronunciation audio for a text content.
    
    Args:
        text_content_id: The ID of the text content
        audio_file_path: Path to the audio file
    """
    backend_url = get_backend_url()
    
    with open(audio_file_path, 'rb') as audio_file:
        files = {'file': (os.path.basename(audio_file_path), audio_file, 'application/octet-stream')}
        response = requests.post(
            f"{backend_url}/text-content/{text_content_id}/upload-pronunciation",
            files=files,
            cookies=_get_request_cookies()
        )
        
        if response.status_code != 200:
            raise Exception(f"Failed to upload pronunciation: {response.text}")


def _upload_transcription(text_content_id, transcription, transcription_system):
    """
    Helper function to upload transcription for a text content.
    
    Args:
        text_content_id: The ID of the text content
        transcription: The transcription text
        transcription_system: The transcription system (e.g., "pinyin", "romaji", "IPA")
    """
    backend_url = get_backend_url()
    
    payload = {
        "transcription": transcription,
        "transcriptionSystem": transcription_system
    }
    
    response = requests.post(
        f"{backend_url}/text-content/{text_content_id}/add-transcription",
        json=payload,
        cookies=_get_request_cookies()
    )
    
    if response.status_code != 200:
        raise Exception(f"Failed to upload transcription: {response.text}")

