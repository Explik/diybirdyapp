# Setup 
1. Install required packages ```npm install```
2. Start localhost server
3. Run openapi-generator-cli command (Requires java 11+) ```npm run generate-api-client```
4. Install modules in shared/api_client ```cd shared/api_client && pip install -e . ```
5. Install Streamlit (for locale manager) ```pip install streamlit```

## Setup Google Cloud Translation API
The translation features require setting up Google Cloud Translation API. Follow the instructions here:
https://docs.cloud.google.com/translate/docs/setup

# Tools

## Locale Manager (Streamlit UI)
Interactive web-based UI for managing Angular i18n translations (XLIFF files).

**Features:**
- Create new locale files
- Edit existing translations with auto-save
- Auto-translate using Google Translate API
- Filter and search translations
- Track translation progress

**Note:** Changes are automatically saved to files. Use Git to track and revert changes.

**Usage:**
```bash
cd tools
streamlit run locale_manager.py
```

See [LOCALE_MANAGER_README.md](./LOCALE_MANAGER_README.md) for detailed documentation.

# Usage 

## Import ANKI set
1. Start localhost server
2. Adapt the conversion template in tools/convert-anki-deck-template to your specific needs
3. Run the conversion script ```py [script path]```
4. Run the import script ```py import-deck [output path of previous script]```

## Import Quizlet set
1. Start localhost server
2. Go to the quizlet set you want to import
3. Run convert-quizlet-set.js in the browser console
4. Save the output to a file
5. Run the import script ```py import-deck [file path]```



# Notes 
pip install google-cloud-translate==2.0.1
