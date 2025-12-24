# Flashcard Import Tool 

## 🚀 Quick Start

This directory contains a **multi-page Streamlit web application** for importing and creating flashcard decks.

### Running the App

```powershell
# Option 1: Use the launcher script (recommended)
.\run.ps1

# Option 2: Run directly
streamlit run app.py
```

See [QUICKSTART.md](QUICKSTART.md) for detailed setup instructions and [README_APP.md](README_APP.md) for full documentation.

📖 **New Features Documentation:**
- [LOCAL_STORAGE_GUIDE.md](LOCAL_STORAGE_GUIDE.md) - Complete guide to local deck storage
- [QUICK_REFERENCE.md](QUICK_REFERENCE.md) - Quick API reference
- [example_local_storage.py](example_local_storage.py) - Working code example

## Features 

### Workflow Overview

The flashcard import tool now uses a **two-step process**:

```
┌─────────────────┐
│  Upload Source  │ (TXT/CSV/Anki file)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Create Locally  │ → deck_storage/My_Deck/
│  - data.json    │      ├── data.json
│  - media files  │      ├── media/
└────────┬────────┘      └── My_Deck.zip
         │
         ▼
┌─────────────────┐
│ Enhance (Opt.)  │ Add pronunciation, images, etc.
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Upload to Server│ When ready
└─────────────────┘
```

**Benefits:**
- Adding additional content (like pronunciation audio) before uploading
- Reviewing and editing the deck locally
- Keeping backups in a standardized format
- Batch processing multiple decks
- Working offline

### Local Storage

Decks are stored locally in the `deck_storage` directory with the following structure:
```
deck_storage/
  Deck_Name/
    data.json          # Flashcard data
    media/             # Media files (audio, images, etc.)
      audio-1.mp3
      image-1.png
  Deck_Name.zip        # ZIP archive ready for distribution
```

### Create deck from .txt file 
The page "Create deck from .txt file" allows users to create flashcard decks from plain text file in the source language. Automatic translation is used to generate the target language text for the flashcards. The text file should contain one sentence per line. 

For example, the following text file in English:
```
He didn't go to hospital.
She is very happy.
That's great!
```

will generate the following flashcard deck with Spanish translations:
```
| English | Spanish          |
|---------|------------------|
| He didn't go to hospital. | Él no fue al hospital. |
| She is very happy.        | Ella está muy feliz.   |
| That's great!             | ¡Eso es genial!        |
```

Generation options: 
- Deck name: The name of the flashcard deck to be created.
- Source language: The language of the input text file. If not specified, the system will attempt to auto-detect the language.
- Target language: The language for the flashcard translations.

**New Features:**
- Decks are saved locally before upload
- Option to upload to server after review
- ZIP archive automatically created
- Deck location shown in summary

### Create deck from .csv file 
The page "Create deck from .CSV file" allows users to create flashcard deck from a CSV file. 

Simple CSV structure:
```
| Source Text | Target Text |
|-------------|-------------|
| Hello       | Hola        |
| Goodbye     | Adiós       |
```

Generation options:
- Skip header row: If enabled, the first row of the CSV file will be treated as header and skipped.
- Deck name: The name of the flashcard deck to be created.
- Left language: The language of the left column text.
- Right language: The language of the right column text.

**New Features:**
- Decks are saved locally before upload
- Option to upload to server after review
- ZIP archive automatically created
- Deck location shown in summary

### Create deck from .anki file 
The page "Create deck from .anki file" allows users to import existing Anki deck files (.apkg) and convert them to the internal storage format. The tool supports various content types including text, audio, image, and video flashcards.

**Features:**
- **Field Mapping**: Select which Anki fields to use for the front and back of flashcards
- **Auto-Detection**: Automatically detects content type (text, audio, image, or video) from field content
- **Language Selection**: Specify languages for text, audio, and video content
- **Pronunciation Support**: Optional pronunciation audio fields for text content
- **Transcription Support**: Optional transcription fields for text content (e.g., pinyin, romaji, IPA)
  - Available only for text content
  - Can be specified for both front and back sides
  - Requires transcription system specification (e.g., "pinyin", "romaji", "IPA")
- **Media Preservation**: Automatically extracts and stores media files from the Anki deck

**Transcription Options:**
When text content is selected for either side:
- **Transcription field**: Select an Anki field containing the transcription text
- **Transcription system**: Specify the transcription system used (e.g., "pinyin" for Chinese, "romaji" for Japanese, "IPA" for phonetic transcription)

### Add pronounciation to flashcards
The page "Add pronounciation to flashcards" allows users to record their own pronounciation for flashcards in a deck. Alternatively, Google's text-to-speech synthesis can be used to generate pronounciation audio for the flashcards.

Options: 
- Select deck: Choose the flashcard deck to add pronounciation to.
- Recording: Record pronounciation audio for each flashcard manually.
- Playback: Play back the recorded for each flashcard.

# Implementation of tool
This section describes the implementation details of the flashcard import tool.

## Module Organization

The tool uses the following modules:

- `import_client.py` - API client for flashcard operations and local deck management (tool-specific)
- `deck_storage.py` - Local deck storage functionality (tool-specific)
- `shared/google_api.py` - Google Translate and Text-to-Speech integration (shared across tools)
- `shared/api_client/` - Auto-generated OpenAPI client for backend communication (shared across tools)

**Note:** `import_client.py` and `deck_storage.py` are specific to this tool and located in the `import-flashcards.py/` directory. Other tools like `import-deck.py` use the basic API client from `shared/import_client.py`.

## Language selection 
The tool allows users to select source and target languages for flashcard generation from the available backend languages. Each language has one or more associated configuration that includes details such as language code. The tool will for now use the first configuration found for a given language.

The tool retrieves the available languages and configurations using the following API endpoints:
```
GET /language # Retrieve available languages
Response:
[
    {
        "id": "en",
        "name": "English",
        "isoCode": "EN"
    },
    ...
]

GET /language/{id}/config?type=google-translate # Retrieve configuration for a specific language
Response:
[
    {
        "id": "config-id",
        "languageId": "en",
        "languageCode": "en-US",
    },
    ... 
]
```

After retrieving the language code, the tool will check if the Google Translate API supports the language code. If not, an error message will be displayed to the user.

# Login 
The tool needs an associated user account to interact with the backend API. The user can log in using the backend url, username and password in the sidebar. This content is persisted across pages using a file on disk (`login_info.json`). The connection to the backend can be tested using the "Test Connection" button in the sidebar.

```
POST /auth/login
Request:
{
    "email": "user",
    "password": "pass"
}

Response:
Cookie JSESSIONID # Session cookie for all subsequent authenticated requests
```

## Internal representation of flashcard decks
The tool represents a flashcard deck as a directory with the following structure:
```
/flashcard-deck.zip
    data.json # Flashcards index
    media-1.mp4 # Media file 1
    media-2.png # Media file 2
    ...
```

The format for data.json is largely based on the FlashcardDeckDto, FlashcardDto, FlashcardContentDto, etc. with a few additions for non-flashcard content, like pronounciation. 
```json
{
    "name": "Deck Name",
    "description": "Description of the deck",
    "flashcards": [
        {
            "id": "flashcard-1",
            "leftContent": {
                "type": "text",
                "content": "Text on the left side",
                "languageId": "backend-language-id",
                "pronounciation": {
                    "content": "media-1.mp4",
                },
                "transcription": {
                    "transcription": "wo1 shi4",
                    "transcriptionSystem": "pinyin"
                }
            },
            "rightContent": {
                "type": "image",
                "content": "media-2.png"
            }
        }
        ...
    ]
}
```

## Upload to server 
The tool uploads the created flashcard deck to the server using the following API endpoint. The tool first creates the flashcard deck on the server, then uploads each flashcard individually.
```
POST /flashcard-deck/ # Create flashcard deck (using FlashcardDeckDto)
POST /flashcard # Create text-text flashcard (using FlashcardDto)
POST /flashcard/rich # Create rich media flashcard (using form with FlashcardDto and media files)
```

