package com.explik.diybirdyapp.manager.configurationManager;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.ConfigurationOptionsDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default configuration manager that handles the initial selection of configuration type.
 */
@Component
public class DefaultConfigurationManager implements ConfigurationManager {
    
    @Override
    public ConfigurationOptionsDto getAvailableOptions(ConfigurationOptionsDto request) {
        // Return all available configuration types
        List<ConfigurationOptionsDto.Option> options = ConfigurationTypes.getAllTypes().stream()
                .map(type -> new ConfigurationOptionsDto.Option(type, type))
                .collect(Collectors.toList());
        
        ConfigurationOptionsDto response = new ConfigurationOptionsDto();
        response.setSelection("configuration-type");
        response.setSelectedOptions(List.of());
        response.setAvailableOptions(options);
        response.setLastSelection(false);
        
        return response;
    }
    
    @Override
    public boolean canHandle(ConfigurationOptionsDto request) {
        // Handle empty requests (initial call) or requests with no selected options
        return request.getSelectedOptions() == null || request.getSelectedOptions().isEmpty();
    }
}
