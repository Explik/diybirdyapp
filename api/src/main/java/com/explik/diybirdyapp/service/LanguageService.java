package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.model.admin.ConfigurationIdentifierDto;
import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.command.*;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.GetAllLanguagesQuery;
import com.explik.diybirdyapp.persistence.query.GetConfigurationByIdQuery;
import com.explik.diybirdyapp.persistence.query.GetLanguageByIdQuery;
import com.explik.diybirdyapp.persistence.query.GetLanguageConfigsQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageService {
    @Autowired
    private QueryHandler<GetLanguageByIdQuery, FlashcardLanguageDto> getLanguageByIdQueryHandler;

    @Autowired
    private QueryHandler<GetAllLanguagesQuery, List<FlashcardLanguageDto>> getAllLanguagesQueryHandler;

    @Autowired
    private QueryHandler<GetLanguageConfigsQuery, List<ConfigurationDto>> getLanguageConfigsQueryHandler;

    @Autowired
    private QueryHandler<GetConfigurationByIdQuery, ConfigurationDto> getConfigurationByIdQueryHandler;

    @Autowired
    private CommandHandler<AddLanguageCommand> addLanguageCommandHandler;

    @Autowired
    private CommandHandler<UpdateLanguageCommand> updateLanguageCommandHandler;

    @Autowired
    private CommandHandler<CreateLanguageConfigCommand> createLanguageConfigCommandHandler;

    @Autowired
    private CommandHandler<AttachLanguageConfigCommand> attachLanguageConfigCommandHandler;

    @Autowired
    private CommandHandler<DetachLanguageConfigCommand> detachLanguageConfigCommandHandler;

    public FlashcardLanguageDto getById(String languageId) {
        var query = new GetLanguageByIdQuery();
        query.setLanguageId(languageId);
        return getLanguageByIdQueryHandler.handle(query);
    }

    public FlashcardLanguageDto create(FlashcardLanguageDto languageModel) {
        var command = new AddLanguageCommand();
        command.setId(languageModel.getId());
        command.setName(languageModel.getName());
        command.setIsoCode(languageModel.getIsoCode());
        addLanguageCommandHandler.handle(command);
        
        // Query the created language
        var query = new GetLanguageByIdQuery();
        query.setLanguageId(command.getResultId());
        return getLanguageByIdQueryHandler.handle(query);
    }

    public FlashcardLanguageDto update(FlashcardLanguageDto languageModel) {
        var command = new UpdateLanguageCommand();
        command.setId(languageModel.getId());
        command.setName(languageModel.getName());
        command.setIsoCode(languageModel.getIsoCode());
        updateLanguageCommandHandler.handle(command);
        
        // Query the updated language
        var query = new GetLanguageByIdQuery();
        query.setLanguageId(command.getResultId());
        return getLanguageByIdQueryHandler.handle(query);
    }

    public List<FlashcardLanguageDto> getAll() {
        var query = new GetAllLanguagesQuery();
        return getAllLanguagesQueryHandler.handle(query);
    }

    public List<ConfigurationDto> getLanguageConfigs(String languageId, String configurationType) {
        var query = new GetLanguageConfigsQuery();
        query.setLanguageId(languageId);
        query.setConfigurationType(configurationType);
        return getLanguageConfigsQueryHandler.handle(query);
    }

    public ConfigurationDto createLanguageConfig(String languageId, ConfigurationDto configModel) {
        var command = new CreateLanguageConfigCommand();
        command.setLanguageId(languageId);
        command.setConfig(configModel);
        createLanguageConfigCommandHandler.handle(command);
        
        // Query the created config
        var query = new GetConfigurationByIdQuery();
        query.setConfigId(command.getResultId());
        return getConfigurationByIdQueryHandler.handle(query);
    }

    public ConfigurationDto attachLanguageConfig(String languageId, ConfigurationIdentifierDto configDto) {
        var command = new AttachLanguageConfigCommand();
        command.setLanguageId(languageId);
        command.setConfigId(configDto.getId());
        attachLanguageConfigCommandHandler.handle(command);
        
        // Query the attached config
        var query = new GetConfigurationByIdQuery();
        query.setConfigId(command.getResultId());
        return getConfigurationByIdQueryHandler.handle(query);
    }

    public void detachLanguageConfig(String languageId, ConfigurationIdentifierDto configDto) {
        var command = new DetachLanguageConfigCommand();
        command.setLanguageId(languageId);
        command.setConfigId(configDto.getId());
        detachLanguageConfigCommandHandler.handle(command);
    }
}
