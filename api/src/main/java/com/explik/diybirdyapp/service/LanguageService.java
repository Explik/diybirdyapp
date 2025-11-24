package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.dto.admin.ConfigurationDto;
import com.explik.diybirdyapp.dto.admin.ConfigurationIdentifierDto;
import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageService {
    @Autowired
    LanguageRepository repository;

    public FlashcardLanguageDto getById(String languageId) {
        return repository.getById(languageId);
    }

    public FlashcardLanguageDto create(FlashcardLanguageDto languageModel) {
        return repository.add(languageModel);
    }

    public FlashcardLanguageDto update(FlashcardLanguageDto languageModel) {
        return repository.update(languageModel);
    }

    public List<FlashcardLanguageDto> getAll() {
        return repository.getAll();
    }

    public List<ConfigurationDto> getLanguageConfigs(String languageId, String configurationType) {
        return repository.getLanguageConfigs(languageId, configurationType);
    }

    public ConfigurationDto createLanguageConfig(String languageId, ConfigurationDto configModel) {
        return repository.createLanguageConfig(languageId, configModel);
    }

    public ConfigurationDto attachLanguageConfig(String languageId, ConfigurationIdentifierDto configDto) {
        return repository.attachLanguageConfig(languageId, configDto.getId());
    }

    public void detachLanguageConfig(String languageId, ConfigurationIdentifierDto configDto) {
        repository.detachLanguageConfig(languageId, configDto.getId());
    }
}
