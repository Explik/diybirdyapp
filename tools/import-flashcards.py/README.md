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
To be added

### Add pronounciation to flashcards
The page "Add pronounciation to flashcards" allows users to record their own pronounciation for flashcards in a deck. Alternatively, Google's text-to-speech synthesis can be used to generate pronounciation audio for the flashcards.

Options: 
- Select deck: Choose the flashcard deck to add pronounciation to.
- Individual recording: Record pronounciation audio for each flashcard manually.
- Individual automatic generation: Use text-to-speech synthesis to generate pronounciation audio for individual flashcards.
- Individual playback: Play back the recorded or generated pronounciation audio for each flashcard.
- Bulk translation: Use text-to-speech synthesis to generate pronounciation audio for all flashcards in the deck.

# Implementation of tool
This section describes the implementation details of the flashcard import tool.

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
                "pronounciation": {
                    "content": "media-1.mp4",
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

