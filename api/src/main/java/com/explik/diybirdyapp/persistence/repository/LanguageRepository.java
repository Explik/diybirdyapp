package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.dto.admin.ConfigurationDto;
import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;

import java.util.List;

public interface LanguageRepository {
    FlashcardLanguageDto add(FlashcardLanguageDto language);
    FlashcardLanguageDto getById(String languageId);
    FlashcardLanguageDto update(FlashcardLanguageDto language);
    List<FlashcardLanguageDto> getAll();

    List<ConfigurationDto> getLanguageConfigs(String languageId, String configurationType);
    ConfigurationDto createLanguageConfig(String languageId, ConfigurationDto configModel);
    ConfigurationDto attachLanguageConfig(String languageId, String configId);
    void detachLanguageConfig(String languageId, String configId);
}
