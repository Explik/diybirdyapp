# Quick Start Guide

## 1. First-time Setup

### Install Dependencies
```powershell
# From the import-flashcards.py directory
pip install -r requirements.txt

# Install the OpenAPI client
cd ..\shared\api_client
pip install -e .
cd ..\..\import-flashcards.py
```

### Set up Google Cloud Credentials
```powershell
# Set environment variable (replace with your path)
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\path\to\your\credentials.json"
```

### Verify API Server
Make sure your API server is running on http://localhost:8080

## 2. Run the App

```powershell
streamlit run app.py
```

The app will open in your browser at http://localhost:8501

## 3. Try the Sample

1. Go to "📝 Create from TXT" in the sidebar
2. Upload `sample_sentences.txt` (included in this directory)
3. Configure:
   - Deck Name: "Test Deck"
   - Source Language: Auto-detect (or English)
   - Target Language: Spanish (or any language you prefer)
4. Click "Generate Flashcard Deck"

## Troubleshooting

### "Could not load languages from API"
- Check if API server is running on port 8080
- Verify you can access http://localhost:8080 in a browser

### "Failed to create flashcard"
- Ensure you're authenticated with the API
- Check API logs for errors

### Translation errors
- Verify GOOGLE_APPLICATION_CREDENTIALS is set correctly
- Ensure Google Translate API is enabled in your GCP project
- Check you have sufficient quota

## Next Steps

Explore other pages to see planned features:
- 📊 Create from CSV
- 📦 Create from Anki
- 🔊 Add Pronunciation
