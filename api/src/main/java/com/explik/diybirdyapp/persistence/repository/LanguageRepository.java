package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.dto.admin.ConfigurationDto;
import com.explik.diybirdyapp.model.content.FlashcardLanguageModel;
import java.util.List;

public interface LanguageRepository {
    FlashcardLanguageModel add(FlashcardLanguageModel language);
    FlashcardLanguageModel getById(String languageId);
    FlashcardLanguageModel update(FlashcardLanguageModel language);
    List<FlashcardLanguageModel> getAll();

    List<ConfigurationDto> getLanguageConfigs(String languageId, String configurationType);
    ConfigurationDto createLanguageConfig(String languageId, ConfigurationDto configModel);
    ConfigurationDto attachLanguageConfig(String languageId, String configId);
    void detachLanguageConfig(String languageId, String configId);
}
