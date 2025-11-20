package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.admin.ConfigurationModel;
import com.explik.diybirdyapp.model.content.FlashcardLanguageModel;
import java.util.List;

public interface LanguageRepository {
    FlashcardLanguageModel add(FlashcardLanguageModel language);
    FlashcardLanguageModel getById(String languageId);
    FlashcardLanguageModel update(FlashcardLanguageModel language);
    List<FlashcardLanguageModel> getAll();

    List<ConfigurationModel> getLanguageConfigs(String languageId, String configurationType);
    ConfigurationModel createLanguageConfig(String languageId, ConfigurationModel configModel);
    ConfigurationModel attachLanguageConfig(String languageId, String configId);
    void detachLanguageConfig(String languageId, String configId);
}
