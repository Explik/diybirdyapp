package com.explik.diybirdyapp.manager.configurationManager;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleTextToSpeechDto;
import com.explik.diybirdyapp.model.admin.ConfigurationOptionsDto;
import com.google.cloud.texttospeech.v1.ListVoicesRequest;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.Voice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration manager for Google Text-to-Speech.
 * Handles the multi-step selection process:
 * 1. Select language code
 * 2. Select voice name (filtered by language code)
 * 3. Return final configuration
 */
@Component
public class GoogleTextToSpeechConfigurationManager implements ConfigurationManager {
    
    @Autowired
    private TextToSpeechClient textToSpeechClient;
    
    @Override
    public ConfigurationOptionsDto getAvailableOptions(ConfigurationOptionsDto request) {
        List<String> selectedOptions = request.getSelectedOptions();
        
        // Step 1: Configuration type has been selected, return available language codes
        if (selectedOptions.size() == 1) {
            return getLanguageCodeOptions(selectedOptions);
        }
        
        // Step 2: Language code has been selected, return available voices for that language
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
                && ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH.equals(selectedOptions.get(0));
    }
    
    private ConfigurationOptionsDto getLanguageCodeOptions(List<String> selectedOptions) {
        // Fetch all available voices from Google Cloud
        ListVoicesRequest voicesRequest = ListVoicesRequest.newBuilder().build();
        var voicesResponse = textToSpeechClient.listVoices(voicesRequest);
        
        // Extract unique language codes
        Set<String> languageCodes = new TreeSet<>();
        for (Voice voice : voicesResponse.getVoicesList()) {
            languageCodes.addAll(voice.getLanguageCodesList());
        }
        
        // Create options
        List<ConfigurationOptionsDto.Option> options = languageCodes.stream()
                .map(langCode -> new ConfigurationOptionsDto.Option(langCode, langCode))
                .collect(Collectors.toList());
        
        ConfigurationOptionsDto response = new ConfigurationOptionsDto();
        response.setSelection("language-code");
        response.setSelectedOptions(selectedOptions);
        response.setAvailableOptions(options);
        response.setLastSelection(false);
        
        return response;
    }
    
    private ConfigurationOptionsDto getVoiceOptions(List<String> selectedOptions) {
        String selectedLanguageCode = selectedOptions.get(1);
        
        // Fetch all available voices from Google Cloud
        ListVoicesRequest voicesRequest = ListVoicesRequest.newBuilder()
                .setLanguageCode(selectedLanguageCode)
                .build();
        var voicesResponse = textToSpeechClient.listVoices(voicesRequest);
        
        // Create voice options with details
        List<ConfigurationOptionsDto.Option> options = voicesResponse.getVoicesList().stream()
                .map(voice -> {
                    Map<String, Object> voiceDetails = new LinkedHashMap<>();
                    voiceDetails.put("name", voice.getName());
                    voiceDetails.put("languageCodes", voice.getLanguageCodesList());
                    voiceDetails.put("ssmlGender", voice.getSsmlGender().name());
                    voiceDetails.put("naturalSampleRateHertz", voice.getNaturalSampleRateHertz());
                    
                    return new ConfigurationOptionsDto.Option(voice.getName(), voiceDetails);
                })
                .collect(Collectors.toList());
        
        ConfigurationOptionsDto response = new ConfigurationOptionsDto();
        response.setSelection("voice-name");
        response.setSelectedOptions(selectedOptions);
        response.setAvailableOptions(options);
        response.setLastSelection(false);
        
        return response;
    }
    
    private ConfigurationOptionsDto getFinalConfiguration(List<String> selectedOptions) {
        String configType = selectedOptions.get(0);
        String languageCode = selectedOptions.get(1);
        String voiceName = selectedOptions.get(2);
        
        // Create the final configuration object
        ConfigurationGoogleTextToSpeechDto config = new ConfigurationGoogleTextToSpeechDto();
        config.setLanguageCode(languageCode);
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
}
