package com.explik.diybirdyapp.manager.configurationManager;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleSpeechToTextDto;
import com.explik.diybirdyapp.model.admin.ConfigurationOptionsDto;
import com.google.cloud.speech.v1.SpeechClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration manager for Google Speech-to-Text.
 * Handles the single-step selection process:
 * 1. Select language code
 * 2. Return final configuration
 */
@Component
public class GoogleSpeechToTextConfigurationManager implements ConfigurationManager {
    
    @Autowired
    private SpeechClient speechClient;
    
    @Override
    public ConfigurationOptionsDto getAvailableOptions(ConfigurationOptionsDto request) {
        List<String> selectedOptions = request.getSelectedOptions();
        
        // Step 1: Configuration type has been selected, return available language codes
        if (selectedOptions.size() == 1) {
            return getLanguageCodeOptions(selectedOptions);
        }
        
        // Step 2: Language code has been selected, return final configuration
        if (selectedOptions.size() == 2) {
            return getFinalConfiguration(selectedOptions);
        }
        
        throw new IllegalArgumentException("Invalid number of selected options: " + selectedOptions.size());
    }
    
    @Override
    public boolean canHandle(ConfigurationOptionsDto request) {
        List<String> selectedOptions = request.getSelectedOptions();
        return selectedOptions != null 
                && !selectedOptions.isEmpty() 
                && ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT.equals(selectedOptions.get(0));
    }
    
    private ConfigurationOptionsDto getLanguageCodeOptions(List<String> selectedOptions) {
        // Google Speech-to-Text supports a defined list of language codes
        // Reference: https://cloud.google.com/speech-to-text/docs/languages
        Set<String> languageCodes = new TreeSet<>(Arrays.asList(
            "af-ZA", "am-ET", "ar-DZ", "ar-BH", "ar-EG", "ar-IQ", "ar-IL", "ar-JO", "ar-KW", "ar-LB",
            "ar-MA", "ar-OM", "ar-QA", "ar-SA", "ar-PS", "ar-TN", "ar-AE", "ar-YE",
            "az-AZ", "bg-BG", "bn-BD", "bn-IN", "bs-BA", "ca-ES", "cs-CZ", "da-DK", "de-AT", "de-DE",
            "el-GR", "en-AU", "en-CA", "en-GH", "en-HK", "en-IN", "en-IE", "en-KE", "en-NZ", "en-NG",
            "en-PK", "en-PH", "en-SG", "en-ZA", "en-TZ", "en-GB", "en-US",
            "es-AR", "es-BO", "es-CL", "es-CO", "es-CR", "es-DO", "es-EC", "es-SV", "es-GT", "es-HN",
            "es-MX", "es-NI", "es-PA", "es-PY", "es-PE", "es-PR", "es-ES", "es-US", "es-UY", "es-VE",
            "et-EE", "eu-ES", "fa-IR", "fi-FI", "fil-PH", "fr-BE", "fr-CA", "fr-FR", "fr-CH",
            "gl-ES", "gu-IN", "he-IL", "hi-IN", "hr-HR", "hu-HU", "hy-AM", "id-ID", "is-IS", "it-IT",
            "ja-JP", "jv-ID", "ka-GE", "km-KH", "kn-IN", "ko-KR", "lo-LA", "lt-LT", "lv-LV",
            "mk-MK", "ml-IN", "mn-MN", "mr-IN", "ms-MY", "my-MM", "ne-NP", "nl-BE", "nl-NL",
            "no-NO", "pl-PL", "pt-BR", "pt-PT", "ro-RO", "ru-RU", "si-LK", "sk-SK", "sl-SI",
            "sq-AL", "sr-RS", "su-ID", "sv-SE", "sw-KE", "sw-TZ", "ta-IN", "ta-MY", "ta-SG", "ta-LK",
            "te-IN", "th-TH", "tr-TR", "uk-UA", "ur-IN", "ur-PK", "uz-UZ", "vi-VN",
            "zh-CN", "zh-TW", "zh-HK", "zu-ZA"
        ));
        
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
    
    private ConfigurationOptionsDto getFinalConfiguration(List<String> selectedOptions) {
        String configType = selectedOptions.get(0);
        String languageCode = selectedOptions.get(1);
        
        // Create the final configuration object
        ConfigurationGoogleSpeechToTextDto config = new ConfigurationGoogleSpeechToTextDto();
        config.setLanguageCode(languageCode);
        
        // Wrap in an option
        ConfigurationOptionsDto.Option option = new ConfigurationOptionsDto.Option(null, config);
        
        ConfigurationOptionsDto response = new ConfigurationOptionsDto();
        response.setSelection("final-configuration");
        response.setSelectedOptions(selectedOptions);
        response.setAvailableOptions(List.of(option));
        response.setLastSelection(true);
        
        return response;
    }
}
