# Flashcard Import Tool - Streamlit App

A multi-page Streamlit application for importing and creating flashcard decks with automatic translation capabilities.

## Features

### ✅ Implemented

**📝 Create deck from .txt file**
- Import plain text files with one sentence per line
- Automatic translation using Google Translate API
- Support for auto-detection of source language
- Progress tracking during deck creation
- Preview file content before processing

### 🚧 Coming Soon

- **📊 Create deck from .csv file**: Import structured CSV data with custom field mapping
- **📦 Create deck from .anki file**: Import existing Anki decks with media preservation
- **🔊 Add pronunciation to flashcards**: Generate text-to-speech audio for flashcards

## Installation

### Prerequisites

1. Python 3.8 or higher
2. Running API server on `http://localhost:8080`
3. Google Cloud credentials (for translation features)

### Setup

1. Navigate to the tool directory:
```bash
cd tools/import-flashcards.py
```

2. Install dependencies:
```bash
pip install -r requirements.txt
```

3. Install the OpenAPI client:
```bash
cd ../shared/api_client
pip install -e .
cd ../../import-flashcards.py
```

4. Set up Google Cloud credentials:
```bash
# Set the environment variable to your Google Cloud credentials JSON file
export GOOGLE_APPLICATION_CREDENTIALS="path/to/your/credentials.json"
```

## Running the Application

Start the Streamlit app:

```bash
streamlit run app.py
```

The application will open in your default web browser at `http://localhost:8501`.

## Usage

### Creating a Deck from TXT File

1. Click on "📝 Create from TXT" in the sidebar
2. Upload a .txt file with one sentence per line
3. Configure the deck settings:
   - Enter a deck name
   - Add an optional description
   - Select source language (or use auto-detect)
   - Select target language for translations
4. Click "Generate Flashcard Deck"
5. Wait for the process to complete
6. Review the summary of created flashcards

### Example Input File

Create a text file (e.g., `sentences.txt`) with content like:

```
He didn't go to hospital.
She is very happy.
That's great!
```

The system will automatically translate these sentences to your selected target language.

## Project Structure

```
import-flashcards.py/
├── app.py                          # Main entry point
├── pages/                          # Streamlit pages
│   ├── 1_📝_Create_from_TXT.py    # TXT file import (implemented)
│   ├── 2_📊_Create_from_CSV.py    # CSV import (placeholder)
│   ├── 3_📦_Create_from_Anki.py   # Anki import (placeholder)
│   └── 4_🔊_Add_Pronunciation.py  # Pronunciation (placeholder)
├── requirements.txt                # Python dependencies
└── README.md                       # This file
```

## Dependencies

The application uses shared modules from the parent `tools` directory:

- `shared.import_client`: API client for flashcard operations
- `shared.google_api`: Google Translate API wrapper
- `shared.api_client.openapi_client`: Auto-generated OpenAPI client

## Troubleshooting

### API Connection Issues

If you see errors about connecting to the API:
- Ensure the API server is running on `http://localhost:8080`
- Check that you can access `http://localhost:8080` in your browser
- Verify firewall settings

### Translation Issues

If translations are not working:
- Verify Google Cloud credentials are properly set
- Check that the Translation API is enabled in your Google Cloud project
- Ensure you have sufficient quota/credits

### Language Not Found

If you get "Language not found" errors:
- Make sure languages are properly configured in the API
- Check the language codes match the API's language list

## Internal Data Format

The tool creates flashcard decks that are compatible with the system's flashcard format:

### FlashcardDeckDto
```json
{
    "id": "unique-id",
    "name": "Deck Name",
    "description": "Description of the deck"
}
```

### FlashcardDto
```json
{
    "id": "unique-id",
    "deckId": "deck-id",
    "deckOrder": 0,
    "frontContent": {
        "type": "text",
        "text": "Text on the front",
        "languageId": "language-id"
    },
    "backContent": {
        "type": "text",
        "text": "Text on the back",
        "languageId": "language-id"
    }
}
```

## Contributing

To add new features:

1. Create a new page in the `pages/` directory following the naming convention `N_Icon_Name.py`
2. Implement the page using Streamlit components
3. Use the shared modules for API interactions
4. Update this README with the new feature

## License

This tool is part of the DIY Birdy App project.
