# Local Deck Storage Guide

## Overview

The flashcard import tool now supports **local deck storage** before uploading to the server. This allows you to:

- Create and review decks locally before upload
- Add additional content (pronunciation, images, etc.) incrementally
- Maintain local backups in a standardized format
- Work offline and upload later
- Share decks as ZIP files

## Storage Format

Decks are stored according to the internal representation described in the README:

```
deck_storage/
  My_Deck_Name/
    data.json          # Flashcard metadata and structure
    media/             # Media files directory
      audio-1.mp3
      audio-2.mp3
      image-1.png
  My_Deck_Name.zip     # Packaged deck ready for distribution
```

### data.json Structure

```json
{
  "name": "My Deck Name",
  "description": "Description of the deck",
  "flashcards": [
    {
      "id": "flashcard-1",
      "deckOrder": 0,
      "frontContent": {
        "type": "text",
        "text": "Hello",
        "languageId": "1",
        "languageName": "English",
        "languageAbbreviation": "en",
        "pronunciation": {
          "content": "media/audio-1.mp3"
        }
      },
      "backContent": {
        "type": "text",
        "text": "Hola",
        "languageId": "2",
        "languageName": "Spanish",
        "languageAbbreviation": "es"
      }
    }
  ]
}
```

## Workflow

### 1. Create Deck Locally

When you use the "Create deck from .txt file" or "Create deck from .csv file" features:

1. Enter deck information (name, languages, etc.)
2. Upload your source file
3. Click "Generate Flashcard Deck"
4. The deck is created in `deck_storage/[Deck_Name]/`
5. A ZIP file is automatically created

### 2. Review and Enhance

After creation, you can:

- Browse to the deck directory
- Review `data.json`
- Add media files to the `media/` directory
- Use the "Add Pronunciation" page to add audio

### 3. Upload to Server

When ready:

1. Click the "Upload to Server" button
2. The deck is uploaded with all flashcards
3. Server returns a deck ID for future reference

## API Reference

### Creating Decks Locally

```python
from import_client import create_local_flashcard_deck

# Create a new local deck
deck_metadata = create_local_flashcard_deck(
    name="My Spanish Deck",
    description="Basic Spanish vocabulary"
)

# Returns:
# {
#     "name": "My Spanish Deck",
#     "description": "Basic Spanish vocabulary",
#     "deck_dir": "C:/path/to/deck_storage/My_Spanish_Deck",
#     "media_dir": "C:/path/to/deck_storage/My_Spanish_Deck/media"
# }
```

### Adding Flashcards

```python
from import_client import (
    add_local_text_flashcard,
    get_language_by_name
)

# Get language objects
english = get_language_by_name("English")
spanish = get_language_by_name("Spanish")

# Add a flashcard
flashcard = add_local_text_flashcard(
    deck_metadata=deck_metadata,
    deck_order=0,
    front_language=english,
    back_language=spanish,
    front_text="Hello",
    back_text="Hola"
)
```

### Adding Pronunciation

```python
from import_client import add_local_pronunciation

# Add pronunciation to the front of a flashcard
media_path = add_local_pronunciation(
    deck_metadata=deck_metadata,
    flashcard_id="flashcard-1",
    side="front",  # or "back"
    audio_file="path/to/hello.mp3"
)

# Returns: "media/hello.mp3"
```

### Creating ZIP Archive

```python
from import_client import create_deck_zip

# Create ZIP file
zip_path = create_deck_zip(
    deck_metadata=deck_metadata,
    output_path=None  # Optional custom path
)

# Returns: "C:/path/to/deck_storage/My_Spanish_Deck.zip"
```

### Viewing Deck Data

```python
from import_client import get_local_deck_data

# Get deck data
deck_data = get_local_deck_data(deck_metadata)

# Access deck information
print(deck_data['name'])
print(deck_data['description'])
print(f"Flashcards: {len(deck_data['flashcards'])}")

# Iterate over flashcards
for flashcard in deck_data['flashcards']:
    print(f"{flashcard['frontContent']['text']} → {flashcard['backContent']['text']}")
```

### Uploading to Server

```python
from import_client import upload_local_deck

# Upload the deck to the server
server_deck = upload_local_deck(deck_metadata)

# Server returns deck object with ID
print(f"Deck uploaded! Server ID: {server_deck.id}")
```

### Listing Local Decks

```python
from import_client import list_local_decks

# Get all local decks
decks = list_local_decks()

for deck in decks:
    print(f"Name: {deck['name']}")
    print(f"Flashcards: {deck['flashcard_count']}")
    print(f"Location: {deck['deck_dir']}")
```

## Example Usage

See `example_local_storage.py` for a complete working example.

To run the example:

```powershell
cd tools\import-flashcards.py
python example_local_storage.py
```

## Benefits

### 1. Incremental Development
Create basic flashcards, then add pronunciation and media later.

### 2. Quality Control
Review generated translations before uploading to the server.

### 3. Offline Work
Create decks without an active server connection. Upload when ready.

### 4. Backup and Sharing
ZIP files can be:
- Backed up to cloud storage
- Shared with others
- Version controlled
- Imported into other systems

### 5. Batch Processing
Create multiple decks locally, review them all, then batch upload.

## File Locations

Default storage location: `[workspace]/deck_storage/`

You can customize this by creating a `DeckStorage` instance:

```python
from deck_storage import DeckStorage

# Custom storage location
storage = DeckStorage(storage_dir="C:/my/custom/path")
```

## Troubleshooting

### Deck directory not found
Ensure the deck was created successfully and check the `deck_dir` in the returned metadata.

### Media files not included in ZIP
Make sure media files are in the `media/` subdirectory of the deck folder.

### Upload fails
Verify:
- API server is running on http://localhost:8080
- Languages used in the deck exist on the server
- Network connectivity is available

### ZIP file is empty
Check that:
- `data.json` exists in the deck directory
- Media files (if any) are in the `media/` subdirectory
- You have write permissions in the storage directory

## Advanced Usage

### Manual Deck Editing

You can manually edit `data.json` to:
- Adjust flashcard order
- Modify text content
- Add custom metadata
- Reference additional media files

After editing, recreate the ZIP:

```python
zip_path = create_deck_zip(deck_metadata)
```

### Custom Media Files

Add any media file type to the `media/` directory and reference it in `data.json`:

```json
{
  "frontContent": {
    "type": "image",
    "content": "media/my-image.png"
  }
}
```

### Batch Upload Script

Create a script to upload multiple decks:

```python
from import_client import list_local_decks, upload_local_deck

decks = list_local_decks()
for deck in decks:
    try:
        server_deck = upload_local_deck({"deck_dir": deck["deck_dir"]})
        print(f"✓ Uploaded: {deck['name']} (ID: {server_deck.id})")
    except Exception as e:
        print(f"✗ Failed: {deck['name']} - {e}")
```

## Future Enhancements

Planned features:
- Import from local ZIP files
- Merge multiple decks
- Export to Anki format
- Deck validation and repair tools
- Media optimization (compression, format conversion)

