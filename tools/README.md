# Setup 
1. Install required packages ```npm install```
2. Start localhost server
3. Run openapi-generator-cli command (Requires java 11+) ```npm run generate-api-client```
4. Install modules in shared/api_client ```cd shared/api_client && pip install -e . ```

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