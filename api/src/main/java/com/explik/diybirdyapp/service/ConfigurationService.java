package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.persistence.command.DeleteConfigurationCommand;
import com.explik.diybirdyapp.persistence.command.UpdateConfigurationCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.generalCommand.SyncCommandHandler;
import com.explik.diybirdyapp.persistence.query.GetConfigurationByIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationService {
    @Autowired
    private QueryHandler<GetConfigurationByIdQuery, ConfigurationDto> getConfigurationByIdQueryHandler;

    @Autowired
    private SyncCommandHandler<UpdateConfigurationCommand, ConfigurationDto> updateConfigurationCommandHandler;

    @Autowired
    private CommandHandler<DeleteConfigurationCommand> deleteConfigurationCommandHandler;

    public ConfigurationDto getById(String configId) {
        var query = new GetConfigurationByIdQuery();
        query.setConfigId(configId);
        return getConfigurationByIdQueryHandler.handle(query);
    }

    public ConfigurationDto update(ConfigurationDto configModel) {
        var command = new UpdateConfigurationCommand();
        command.setConfiguration(configModel);
        return updateConfigurationCommandHandler.handle(command);
    }

    public void deleteById(String configId) {
        var command = new DeleteConfigurationCommand();
        command.setConfigId(configId);
        deleteConfigurationCommandHandler.handle(command);
    }
}
