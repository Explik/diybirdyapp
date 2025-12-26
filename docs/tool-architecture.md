# Tools 
Tools are non-essential components of the application that provide additional functionality to enhance user experience.
This includes configuration management, data import, etc. 

## Principles 
- *Tools should be single-purpose*. For each task, there should be a dedicated tool. 
- *Tools should not hold any authentation*. Authentication for third-party services should be handled by the main application. The user should only need to login into the backend and the backend handles authentication for the third-party services on behalf of the user using the tool.

## Backend services 
The tools can use services provided by the backend.

### Chat service (AI assistant)


### Configuration service
Configurations are used to store settings for third-party services and are handled via the CRUD API at `/api/configuration`.
However, selection of configuration values is a multi-step process, where each step depends on the previous selection. So to facilitate this, there is an additional API endpoint at `/api/configuration/available-options` that allows the frontend to query for available options based on previous selections.

The process works as follows:
```
# First call contains an empty body to get the available configuration types
POST /api/configuration/available-options
{
}

returns 
{
    "selection": "configuration-type",
    "selectedOptions": [],
    "availableOptions": [
        {
            "id": "CONFIG_TYPE_1",
            "value": "CONFIG_TYPE_1"
        },
        ...
    ],
    "lastSelection": false
}

# Subsequent calls contain the selected options to get the next level of available options
# Each seelcted option is appended to the selectedOptions array
POST /api/configuration/available-options
{
    "selection": "configuration-type",
    "selectedOptions": ["CONFIG_TYPE_1"]
}

returns 
{
    "selection": "google-translate-1",
    "selectedOptions": ["TEXT_TO_SPEECH_CONFIG"],
    "availableOptions": [
        {
            "id": "azl",
            "value": {
                "type":"google-text-to-speech"
                "languageCode":"af-ZA"
                "voiceName":"af-ZA-Standard-A"
            }
        },
        ...
    ],
    "lastSelection": false
}

# Final call contain fully-formed configuration objects that can be saved
POST /api/configuration/available-options
{
    "selection": "google-translate-1",
    "selectedOptions": ["CONFIG_TYPE_1", "OPTION_1_A", "OPTION_2_B"],
    "availableOptions": [
        {
            "id": null,
            "value": {
                "type": "CONFIG_TYPE_1",
                "optionA": "OPTION_1_A",
                "optionB": "OPTION_2_B"
            }
        }
    ],
    lastSelection": true
}

# Save the configuration
POST /api/configuration
{
    "type": "CONFIG_TYPE_1",
    "optionA": "OPTION_1_A",
    "optionB": "OPTION_2_B"
}
```

### Languages 


### Translation 
Translation is provided via the `/api/translation` endpoint. It uses the configured translation service to translate text. Currently, only Google Translate is supported.

Example request:
```
# Using system language IDs text can be translated
POST /api/translation/translate 
{
    "sourceLanguageId": "langId1",
    "targetLanguageId": "langId2",
    "text": "Hello, world!"
}

returns 
{
    "translatedText": "Hola, mundo!"
}
```


