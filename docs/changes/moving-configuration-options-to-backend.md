# Configuration Options Implementation Guide

## Overview
This document describes the implementation of the dynamic configuration options system that allows the backend to provide multi-step configuration workflows for different service types (Google Text-to-Speech, Google Translate, etc.).

## Architecture

### Pattern: ConfigurationManager
The system uses a **ConfigurationManager** pattern where each configuration type can implement its own multi-step selection workflow.

```
Client Request → ConfigurationService → Appropriate ConfigurationManager → Google Cloud API → Response
```

## Backend Implementation (Java)

### 1. Core Interface
**Location:** `api/src/main/java/com/explik/diybirdyapp/manager/configurationManager/ConfigurationManager.java`

```java
public interface ConfigurationManager {
    ConfigurationOptionsDto getAvailableOptions(ConfigurationOptionsDto request);
    boolean canHandle(ConfigurationOptionsDto request);
}
```

### 2. Configuration Managers

#### DefaultConfigurationManager
**Location:** `api/src/main/java/com/explik/diybirdyapp/manager/configurationManager/DefaultConfigurationManager.java`

- **Purpose:** Handles initial selection (empty request)
- **Returns:** List of all available configuration types
- **Handles:** Requests with no selected options or null selected options

#### GoogleTextToSpeechConfigurationManager
**Location:** `api/src/main/java/com/explik/diybirdyapp/manager/configurationManager/GoogleTextToSpeechConfigurationManager.java`

- **Purpose:** Manages Google Text-to-Speech configuration selection
- **Selection Flow:**
  1. **Step 1:** User selects configuration type → Returns available language codes
  2. **Step 2:** User selects language code → Returns available voices for that language
  3. **Step 3:** User selects voice → Returns final `ConfigurationGoogleTextToSpeechDto` object
- **Handles:** Requests where first selected option is `"google-text-to-speech"`
- **Data Source:** Google Cloud Text-to-Speech API via `TextToSpeechClient`

#### GoogleTranslateConfigurationManager
**Location:** `api/src/main/java/com/explik/diybirdyapp/manager/configurationManager/GoogleTranslateConfigurationManager.java`

- **Purpose:** Manages Google Translate configuration selection
- **Selection Flow:**
  1. **Step 1:** User selects configuration type → Returns available language codes
  2. **Step 2:** User selects language code → Returns final `ConfigurationGoogleTranslateDto` object
- **Handles:** Requests where first selected option is `"google-translate"`
- **Data Source:** Google Cloud Translation API via `TranslationServiceClient`
- **Requirements:** Requires `GOOGLE_CLOUD_PROJECT` environment variable or `google.cloud.project-id` property

#### GoogleTranslateConfigurationManager
**Location:** `api/src/main/java/com/explik/diybirdyapp/manager/configurationManager/GoogleTranslateConfigurationManager.java`

- **Purpose:** Manages Google Translate configuration selection
- **Selection Flow:**
  1. **Step 1:** User selects configuration type → Returns available language codes
  2. **Step 2:** User selects language code → Returns final `ConfigurationGoogleTranslateDto` object
- **Handles:** Requests where first selected option is `"google-translate"`
- **Data Source:** Google Cloud Translation API via `TranslationServiceClient`

#### MicrosoftTextToSpeechConfigurationManager
**Location:** `api/src/main/java/com/explik/diybirdyapp/manager/configurationManager/MicrosoftTextToSpeechConfigurationManager.java`

- **Purpose:** Manages Microsoft Text-to-Speech configuration selection
- **Selection Flow:**
  1. **Step 1:** User selects configuration type → Returns available locales
  2. **Step 2:** User selects locale → Returns available voices for that locale
  3. **Step 3:** User selects voice → Returns final `ConfigurationMicrosoftTextToSpeechDto` object
- **Handles:** Requests where first selected option is `"microsoft-text-to-speech"`
- **Data Source:** Azure Speech Service REST API (voices list endpoint)
- **Requirements:** Requires `AZURE_SPEECH_KEY` and `AZURE_SPEECH_REGION` environment variables
- **Implementation Notes:** Uses Java's HttpClient to call the REST API directly for fetching voices

### 3. Service Integration
**Location:** `api/src/main/java/com/explik/diybirdyapp/service/ConfigurationService.java`

```java
@Autowired
private List<ConfigurationManager> configurationManagers;

public ConfigurationOptionsDto getAvailableOptions(ConfigurationOptionsDto configOptionsDto) {
    for (ConfigurationManager manager : configurationManagers) {
        if (manager.canHandle(configOptionsDto)) {
            return manager.getAvailableOptions(configOptionsDto);
        }
    }
    throw new IllegalArgumentException("No configuration manager found");
}
```

Spring automatically discovers all `@Component` classes implementing `ConfigurationManager` and injects them as a list.

### 4. Controller Endpoint
**Location:** `api/src/main/java/com/explik/diybirdyapp/controller/ConfigurationController.java`

```java
@PostMapping("/config/available-options")
public ResponseEntity<ConfigurationOptionsDto> getAvailableOptions(
    @RequestBody ConfigurationOptionsDto configOptionsDto
) {
    var options = service.getAvailableOptions(configOptionsDto);
    return ResponseEntity.ok(options);
}
```

## Frontend Implementation (Python)

### 1. API Client Update
The Python API client was regenerated from the OpenAPI specification to include the new endpoint:

```bash
npm run generate-api-client
```

This creates the client at: `tools/shared/api_client/`

### 2. LanguageClient Integration
**Location:** `tools/shared/language_client.py`

Added method:
```python
def get_available_config_options(self, selected_options: List[str]) -> Dict:
    url = f"{self.base_url}/config/available-options"
    payload = {"selectedOptions": selected_options}
    response = requests.post(url, json=payload, cookies=self._get_cookies(), timeout=self.timeout)
    response.raise_for_status()
    return response.json()
```

### 3. Config Editor UI
**Location:** `tools/manage-languages.py/config_editor.py`

#### Key Features:
- **Cascading Dropdowns:** Language selection shows first, voice selection appears once language is chosen
- **Smart Caching:** Each language's voices cached separately (e.g., `tts_voices_en-US`)
- **Human-Readable Display:** Language codes formatted as "English (United States) - en-US"
- **No Buttons Needed:** Dropdowns cascade naturally based on selections

#### Implementation:
```python
# Step 1: Language Code Selection
response = language_client.get_available_config_options([config_type_key])
language_codes = [opt["id"] for opt in response["availableOptions"]]

# Step 2: Voice Selection (triggered by language selection)
if selected_language_code:
    response = language_client.get_available_config_options([config_type_key, selected_language_code])
    voices = response["availableOptions"]
```

### 4. Module Reloading
**Location:** `tools/manage-languages.py/pages/*.py`

To handle Streamlit's module caching:
```python
import importlib
from shared import language_client
importlib.reload(language_client)
from shared.language_client import LanguageClient, CONFIG_TYPES
```

## Data Flow

### Request/Response Structure

**Request:**
```json
{
  "selectedOptions": ["google-text-to-speech"]
}
```

**Response:**
```json
{
  "selection": "language-code",
  "selectedOptions": ["google-text-to-speech"],
  "availableOptions": [
    {"id": "en-US", "option": "en-US"},
    {"id": "es-ES", "option": "es-ES"}
  ],
  "lastSelection": false
}
```

**Final Step Response:**
```json
{
  "selection": "final-configuration",
  "selectedOptions": ["google-text-to-speech", "en-US", "en-US-Standard-A"],
  "availableOptions": [
    {
      "id": null,
      "option": {
        "type": "google-text-to-speech",
        "languageCode": "en-US",
        "voiceName": "en-US-Standard-A"
      }
    }
  ],
  "lastSelection": true
}
```

## Adding New Configuration Types

### Backend Steps:

1. **Create ConfigurationManager Implementation**
   ```java
   @Component
   public class GoogleTranslateConfigurationManager implements ConfigurationManager {
       @Override
       public boolean canHandle(ConfigurationOptionsDto request) {
           return request.getSelectedOptions() != null 
               && !request.getSelectedOptions().isEmpty() 
               && ConfigurationTypes.GOOGLE_TRANSLATE.equals(request.getSelectedOptions().get(0));
       }
       
       @Override
       public ConfigurationOptionsDto getAvailableOptions(ConfigurationOptionsDto request) {
           // Implement multi-step logic
       }
   }
   ```

2. **Register in ConfigurationTypes** (Already exists)
   ```java
   public static final String GOOGLE_TRANSLATE = "google-translate";
   ```

3. **Add Google Cloud Client Dependency** (if needed)
   ```xml
   <dependency>
       <groupId>com.google.cloud</groupId>
       <artifactId>google-cloud-translate</artifactId>
   </dependency>
   ```

4. **Create Bean for Google Cloud Client** (if needed)
   ```java
   @Bean
   public Translate translateClient() throws IOException {
       GoogleCredentials credentials = getGoogleCredentials();
       TranslateOptions options = TranslateOptions.newBuilder()
               .setCredentials(credentials)
               .build();
       return options.getService();
   }
   ```

5. **Spring Auto-Discovery:** That's it! Spring will automatically pick up the new `@Component`

### Frontend Steps:

1. **Regenerate API Client**
   ```bash
   npm run generate-api-client
   ```

2. **Update config_editor.py**
   ```python
   elif config_type_key == "google-translate":
       # Fetch options from backend
       response = language_client.get_available_config_options([config_type_key])
       language_codes = [opt["id"] for opt in response["availableOptions"]]
       
       # Render dropdown
       language_code = st.selectbox("Language", options=language_codes)
       config_data["languageCode"] = language_code
   ```

## API Version Notes

### Google Translate
The implementation uses **Translation API v2** (not v3) for simplicity and consistency with the Python tools. Key benefits:
- **No project ID required** - v2 API doesn't need `GOOGLE_CLOUD_PROJECT` configuration
- **Simpler API** - Direct `listSupportedLanguages()` call without complex request builders
- **Consistent with tools** - Python tools (`locale-manager.py`, etc.) also use v2

The v2 API uses the `com.google.cloud.translate.Translate` client instead of `TranslationServiceClient`.

### Microsoft Azure Speech Service
The implementation uses the **REST API** for listing voices instead of the Java SDK:
- **Simpler Implementation** - Direct HTTP call without complex SDK initialization
- **Lightweight** - No need to create and manage Speech SDK client instances for configuration purposes
- **Standard Approach** - Follows Microsoft's documented REST API pattern
- **Environment Variables** - Uses `AZURE_SPEECH_KEY` and `AZURE_SPEECH_REGION` for authentication

The REST API endpoint format: `https://{region}.tts.speech.microsoft.com/cognitiveservices/voices/list`

For actual speech synthesis, the Java SDK (`com.microsoft.cognitiveservices.speech`) is still used in `MicrosoftTextToSpeechService`.

## Benefits

1. **Centralized Logic:** Configuration workflows managed in backend
2. **Single Source of Truth:** Backend determines available options
3. **Simplified Tools:** Python tools don't need Google Cloud SDK
4. **Consistent Data:** All clients get same options
5. **Extensible:** Easy to add new configuration types
6. **Authentication Centralized:** Only backend needs cloud credentials

## Testing

### Backend Test Flow:
1. Start backend: `mvn spring-boot:run`
2. Test endpoint:
   ```bash
   curl -X POST http://localhost:8080/config/available-options \
     -H "Content-Type: application/json" \
     -d '{"selectedOptions": []}'
   ```

### Frontend Test Flow:
1. Start Streamlit: `streamlit run tools/manage-languages.py/app.py`
2. Navigate to "Create Config" page
3. Select "Google Text-to-Speech"
4. Verify language dropdown populates
5. Select language and verify voice dropdown populates

## Known Issues & Solutions

### Issue: Python Module Caching
**Problem:** Streamlit caches imported modules, changes not picked up
**Solution:** Added `importlib.reload()` in page files

### Issue: Variable Initialization
**Problem:** `UnboundLocalError` if conditions don't execute
**Solution:** Initialize all variables before conditional blocks

## Related Documentation

- [Backend Architecture](backend-architecture.md)
- [Tool Architecture](tool-architecture.md)
- [Testing Strategy](testing-strategy.md)
