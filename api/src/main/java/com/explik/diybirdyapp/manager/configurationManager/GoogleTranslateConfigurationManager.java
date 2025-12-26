package com.explik.diybirdyapp.manager.configurationManager;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleTranslateDto;
import com.explik.diybirdyapp.model.admin.ConfigurationOptionsDto;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuration manager for Google Translate.
 * Handles the selection process:
 * 1. Select language code
 * 2. Return final configuration
 */
@Component
public class GoogleTranslateConfigurationManager implements ConfigurationManager {
    
    @Autowired
    private Translate translateClient;
    
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
                && ConfigurationTypes.GOOGLE_TRANSLATE.equals(selectedOptions.get(0));
    }
    
    private ConfigurationOptionsDto getLanguageCodeOptions(List<String> selectedOptions) {
        // Fetch supported languages from Google Cloud Translate API v2
        // No project ID required for v2 API
        List<Language> languages = translateClient.listSupportedLanguages();
        
        // Create options from supported languages
        List<ConfigurationOptionsDto.Option> options = languages.stream()
                .map(language -> new ConfigurationOptionsDto.Option(
                        language.getCode(), 
                        language.getCode()
                ))
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
        ConfigurationGoogleTranslateDto config = new ConfigurationGoogleTranslateDto();
        config.setLanguageCode(languageCode);
        
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
