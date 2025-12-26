package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.model.admin.ConfigurationOptionsDto;
import com.explik.diybirdyapp.persistence.command.DeleteConfigurationCommand;
import com.explik.diybirdyapp.persistence.command.UpdateConfigurationCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.GetConfigurationByIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.manager.configurationManager.ConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigurationService {
    @Autowired
    private QueryHandler<GetConfigurationByIdQuery, ConfigurationDto> getConfigurationByIdQueryHandler;

    @Autowired
    private CommandHandler<UpdateConfigurationCommand> updateConfigurationCommandHandler;

    @Autowired
    private CommandHandler<DeleteConfigurationCommand> deleteConfigurationCommandHandler;
    
    @Autowired
    private List<ConfigurationManager> configurationManagers;

    public ConfigurationDto getById(String configId) {
        var query = new GetConfigurationByIdQuery();
        query.setConfigId(configId);
        return getConfigurationByIdQueryHandler.handle(query);
    }

    public ConfigurationDto update(ConfigurationDto configModel) {
        var command = new UpdateConfigurationCommand();
        command.setConfiguration(configModel);
        updateConfigurationCommandHandler.handle(command);
        
        // Query the updated config
        var query = new GetConfigurationByIdQuery();
        query.setConfigId(command.getResultId());
        return getConfigurationByIdQueryHandler.handle(query);
    }

    public void deleteById(String configId) {
        var command = new DeleteConfigurationCommand();
        command.setConfigId(configId);
        deleteConfigurationCommandHandler.handle(command);
    }

    public ConfigurationOptionsDto getAvailableOptions(ConfigurationOptionsDto configOptionsDto) {
        // Find the appropriate manager for this request
        for (ConfigurationManager manager : configurationManagers) {
            if (manager.canHandle(configOptionsDto)) {
                return manager.getAvailableOptions(configOptionsDto);
            }
        }
        
        throw new IllegalArgumentException("No configuration manager found for request: " + configOptionsDto.getSelection());
    }
}
