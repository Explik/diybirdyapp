package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.admin.ConfigurationIdentifierModel;
import com.explik.diybirdyapp.model.admin.ConfigurationModel;
import com.explik.diybirdyapp.model.content.FlashcardLanguageModel;
import com.explik.diybirdyapp.persistence.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageService {
    @Autowired
    LanguageRepository repository;

    public FlashcardLanguageModel getById(String languageId) {
        return repository.getById(languageId);
    }

    public FlashcardLanguageModel create(FlashcardLanguageModel languageModel) {
        return repository.add(languageModel);
    }

    public FlashcardLanguageModel update(FlashcardLanguageModel languageModel) {
        return repository.update(languageModel);
    }

    public List<FlashcardLanguageModel> getAll() {
        return repository.getAll();
    }

    public List<ConfigurationModel> getLanguageConfigs(String languageId, String configurationType) {
        return repository.getLanguageConfigs(languageId, configurationType);
    }

    public ConfigurationModel createLanguageConfig(String languageId, ConfigurationModel configModel) {
        return repository.createLanguageConfig(languageId, configModel);
    }

    public ConfigurationModel attachLanguageConfig(String languageId, ConfigurationIdentifierModel configDto) {
        return repository.attachLanguageConfig(languageId, configDto.getId());
    }

    public void detachLanguageConfig(String languageId, ConfigurationIdentifierModel configDto) {
        repository.detachLanguageConfig(languageId, configDto.getId());
    }
}
