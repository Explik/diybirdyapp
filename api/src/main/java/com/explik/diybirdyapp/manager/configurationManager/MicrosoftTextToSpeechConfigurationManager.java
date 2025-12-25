package com.explik.diybirdyapp.manager.configurationManager;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.ConfigurationMicrosoftTextToSpeechDto;
import com.explik.diybirdyapp.model.admin.ConfigurationOptionsDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration manager for Microsoft Text-to-Speech.
 * Handles the multi-step selection process:
 * 1. Select language/locale
 * 2. Select voice name (filtered by locale)
 * 3. Return final configuration
 */
@Component
public class MicrosoftTextToSpeechConfigurationManager implements ConfigurationManager {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public MicrosoftTextToSpeechConfigurationManager() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public ConfigurationOptionsDto getAvailableOptions(ConfigurationOptionsDto request) {
        List<String> selectedOptions = request.getSelectedOptions();
        
        // Step 1: Configuration type has been selected, return available locales
        if (selectedOptions.size() == 1) {
            return getLocaleOptions(selectedOptions);
        }
        
        // Step 2: Locale has been selected, return available voices for that locale
        if (selectedOptions.size() == 2) {
            return getVoiceOptions(selectedOptions);
        }
        
        // Step 3: Voice has been selected, return final configuration
        if (selectedOptions.size() == 3) {
            return getFinalConfiguration(selectedOptions);
        }
        
        throw new IllegalArgumentException("Invalid number of selected options: " + selectedOptions.size());
    }
    
    @Override
    public boolean canHandle(ConfigurationOptionsDto request) {
        List<String> selectedOptions = request.getSelectedOptions();
        return selectedOptions != null 
                && !selectedOptions.isEmpty() 
                && ConfigurationTypes.MICROSOFT_TEXT_TO_SPEECH.equals(selectedOptions.get(0));
    }
    
    private ConfigurationOptionsDto getLocaleOptions(List<String> selectedOptions) {
        try {
            // Fetch all available voices from Microsoft Azure Speech Service
            List<JsonNode> voices = fetchVoicesFromAzure();
            
            // Extract unique locales
            Set<String> locales = new TreeSet<>();
            for (JsonNode voice : voices) {
                locales.add(voice.get("Locale").asText());
            }
            
            // Create options
            List<ConfigurationOptionsDto.Option> options = locales.stream()
                    .map(locale -> new ConfigurationOptionsDto.Option(locale, locale))
                    .collect(Collectors.toList());
            
            ConfigurationOptionsDto response = new ConfigurationOptionsDto();
            response.setSelection("locale");
            response.setSelectedOptions(selectedOptions);
            response.setAvailableOptions(options);
            response.setLastSelection(false);
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch locales from Azure Speech Service", e);
        }
    }
    
    private ConfigurationOptionsDto getVoiceOptions(List<String> selectedOptions) {
        try {
            String selectedLocale = selectedOptions.get(1);
            
            // Fetch all available voices from Microsoft Azure Speech Service
            List<JsonNode> voices = fetchVoicesFromAzure();
            
            // Filter voices by selected locale and create voice options with details
            List<ConfigurationOptionsDto.Option> options = voices.stream()
                    .filter(voice -> voice.get("Locale").asText().equals(selectedLocale))
                    .map(voice -> {
                        Map<String, Object> voiceDetails = new LinkedHashMap<>();
                        voiceDetails.put("name", voice.get("ShortName").asText());
                        voiceDetails.put("displayName", voice.get("DisplayName").asText());
                        voiceDetails.put("locale", voice.get("Locale").asText());
                        voiceDetails.put("localeName", voice.get("LocaleName").asText());
                        voiceDetails.put("gender", voice.get("Gender").asText());
                        
                        if (voice.has("VoiceType")) {
                            voiceDetails.put("voiceType", voice.get("VoiceType").asText());
                        }
                        
                        if (voice.has("StyleList")) {
                            List<String> styles = new ArrayList<>();
                            voice.get("StyleList").forEach(style -> styles.add(style.asText()));
                            voiceDetails.put("styles", styles);
                        }
                        
                        return new ConfigurationOptionsDto.Option(
                                voice.get("ShortName").asText(), 
                                voiceDetails
                        );
                    })
                    .collect(Collectors.toList());
            
            ConfigurationOptionsDto response = new ConfigurationOptionsDto();
            response.setSelection("voice-name");
            response.setSelectedOptions(selectedOptions);
            response.setAvailableOptions(options);
            response.setLastSelection(false);
            
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch voices from Azure Speech Service", e);
        }
    }
    
    private ConfigurationOptionsDto getFinalConfiguration(List<String> selectedOptions) {
        String voiceName = selectedOptions.get(2);
        
        // Create the final configuration object
        ConfigurationMicrosoftTextToSpeechDto config = new ConfigurationMicrosoftTextToSpeechDto();
        config.setVoiceName(voiceName);
        
        ConfigurationOptionsDto response = new ConfigurationOptionsDto();
        response.setSelection("final-configuration");
        response.setSelectedOptions(selectedOptions);
        response.setAvailableOptions(List.of(
                new ConfigurationOptionsDto.Option(null, config)
        ));
        response.setLastSelection(true);
        
        return response;
    }
    
    /**
     * Fetches the list of available voices from Azure Speech Service REST API
     * See: https://learn.microsoft.com/en-us/azure/ai-services/speech-service/rest-text-to-speech?tabs=streaming#get-a-list-of-voices
     */
    private List<JsonNode> fetchVoicesFromAzure() throws IOException, InterruptedException {
        String region = System.getenv("AZURE_SPEECH_REGION");
        String subscriptionKey = System.getenv("AZURE_SPEECH_KEY");
        
        if (region == null || region.isEmpty()) {
            throw new IllegalStateException("AZURE_SPEECH_REGION environment variable is not set");
        }
        
        if (subscriptionKey == null || subscriptionKey.isEmpty()) {
            throw new IllegalStateException("AZURE_SPEECH_KEY environment variable is not set");
        }
        
        String url = String.format("https://%s.tts.speech.microsoft.com/cognitiveservices/voices/list", region);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Ocp-Apim-Subscription-Key", subscriptionKey)
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch voices from Azure. Status: " + response.statusCode() + ", Body: " + response.body());
        }
        
        // Parse JSON response
        JsonNode rootNode = objectMapper.readTree(response.body());
        List<JsonNode> voices = new ArrayList<>();
        rootNode.forEach(voices::add);
        
        return voices;
    }
}
