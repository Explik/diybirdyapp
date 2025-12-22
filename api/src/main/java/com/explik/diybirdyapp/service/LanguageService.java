package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.model.admin.ConfigurationIdentifierDto;
import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.command.*;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.generalCommand.SyncCommandHandler;
import com.explik.diybirdyapp.persistence.query.GetAllLanguagesQuery;
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
    private SyncCommandHandler<AddLanguageCommand, FlashcardLanguageDto> addLanguageCommandHandler;

    @Autowired
    private SyncCommandHandler<UpdateLanguageCommand, FlashcardLanguageDto> updateLanguageCommandHandler;

    @Autowired
    private SyncCommandHandler<CreateLanguageConfigCommand, ConfigurationDto> createLanguageConfigCommandHandler;

    @Autowired
    private SyncCommandHandler<AttachLanguageConfigCommand, ConfigurationDto> attachLanguageConfigCommandHandler;

    @Autowired
    private CommandHandler<DetachLanguageConfigCommand> detachLanguageConfigCommandHandler;

    public FlashcardLanguageDto getById(String languageId) {
        var query = new GetLanguageByIdQuery();
        query.setLanguageId(languageId);
        return getLanguageByIdQueryHandler.handle(query);
    }

    public FlashcardLanguageDto create(FlashcardLanguageDto languageModel) {
        var command = new AddLanguageCommand();
        command.setLanguage(languageModel);
        return addLanguageCommandHandler.handle(command);
    }

    public FlashcardLanguageDto update(FlashcardLanguageDto languageModel) {
        var command = new UpdateLanguageCommand();
        command.setLanguage(languageModel);
        return updateLanguageCommandHandler.handle(command);
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
        return createLanguageConfigCommandHandler.handle(command);
    }

    public ConfigurationDto attachLanguageConfig(String languageId, ConfigurationIdentifierDto configDto) {
        var command = new AttachLanguageConfigCommand();
        command.setLanguageId(languageId);
        command.setConfigId(configDto.getId());
        return attachLanguageConfigCommandHandler.handle(command);
    }

    public void detachLanguageConfig(String languageId, ConfigurationIdentifierDto configDto) {
        var command = new DetachLanguageConfigCommand();
        command.setLanguageId(languageId);
        command.setConfigId(configDto.getId());
        detachLanguageConfigCommandHandler.handle(command);
    }
}
