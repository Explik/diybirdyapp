package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.dto.admin.ConfigurationDto;
import com.explik.diybirdyapp.dto.admin.ConfigurationGoogleTextToSpeechDto;
import com.explik.diybirdyapp.dto.admin.ConfigurationGoogleTranslateDto;
import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FlashcardLanguageRepositoryImpl implements LanguageRepository, ConfigurationRepository {
    private final GraphTraversalSource traversalSource;

    public FlashcardLanguageRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardLanguageDto add(FlashcardLanguageDto language) {
        // Check for duplicates
        if (LanguageVertex.findById(traversalSource, language.getId()) != null)
            throw new IllegalArgumentException("Language with id " + language.getId() + " already exists");
        if (LanguageVertex.findByName(traversalSource, language.getName()) != null)
            throw new IllegalArgumentException("Language with name " + language.getName() + " already exists");
        if (LanguageVertex.findByIsoCode(traversalSource, language.getIsoCode()) != null)
            throw new IllegalArgumentException("Language with isoCode " + language.getIsoCode() + " already exists");

        // Add language to database
        var vertex = LanguageVertex.create(traversalSource);
        vertex.setId(language.getId());
        vertex.setName(language.getName());
        vertex.setIsoCode(language.getIsoCode());

        return createLanguageModel(vertex);
    }

    @Override
    public FlashcardLanguageDto getById(String languageId) {
        var vertex = LanguageVertex.findById(traversalSource, languageId);
        if (vertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        return createLanguageModel(vertex);
    }

    @Override
    public ConfigurationDto get(String configId) {
        var configurationVertex = ConfigurationVertex.findById(traversalSource, configId);
        if (configurationVertex == null)
            throw new IllegalArgumentException("Configuration with id " + configId + " does not exist");

        return createConfigModel(null, configurationVertex);
    }

    @Override
    public ConfigurationDto update(ConfigurationDto configModel) {
        var configurationVertex = ConfigurationVertex.findById(traversalSource, configModel.getId());
        if (configurationVertex == null)
            throw new IllegalArgumentException("Configuration with id " + configModel.getId() + " does not exist");

        updateConfigVertex(configurationVertex, configModel);

        return createConfigModel(null, configurationVertex);
    }

    @Override
    public void delete(String configId) {
        var configurationVertex = ConfigurationVertex.findById(traversalSource, configId);
        if (configurationVertex == null)
            throw new IllegalArgumentException("Configuration with id " + configId + " does not exist");

        configurationVertex.delete();
    }

    @Override
    public FlashcardLanguageDto update(FlashcardLanguageDto language) {
        var vertex = LanguageVertex.findById(traversalSource, language.getId());
        if (vertex == null)
            throw new IllegalArgumentException("Language with id " + language.getId() + " does not exist");

        if (language.getName() != null)
            vertex.setName(language.getName());
        if (language.getIsoCode() != null)
            vertex.setIsoCode(language.getIsoCode());

        return createLanguageModel(vertex);
    }

    @Override
    public List<FlashcardLanguageDto> getAll() {
        var vertices = LanguageVertex.findAll(traversalSource);

        return vertices
            .stream()
            .map(FlashcardLanguageRepositoryImpl::createLanguageModel)
            .toList();
    }

    @Override
    public List<ConfigurationDto> getLanguageConfigs(String languageId, String configurationType) {
        var languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        List<ConfigurationVertex> configurationVertices;
        if (configurationType == null) {
            configurationVertices = ConfigurationVertex.findByLanguage(languageVertex);
        } else {
            configurationVertices = ConfigurationVertex.findByLanguageAndType(languageVertex, configurationType);
        }

        return configurationVertices
            .stream()
            .map(v -> createConfigModel(languageId, v))
            .toList();
    }

    @Override
    public ConfigurationDto createLanguageConfig(String languageId, ConfigurationDto configModel) {
        var languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        if (configModel.getId() == null)
            configModel.setId(UUID.randomUUID().toString());

        var configurationVertex = ConfigurationVertex.create(traversalSource);
        configurationVertex.setId(configModel.getId());
        updateConfigVertex(configurationVertex, configModel);
        configurationVertex.addLanguage(languageVertex);

        return createConfigModel(languageId, configurationVertex);
    }

    @Override
    public ConfigurationDto attachLanguageConfig(String languageId, String configId) {
        var languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        var configurationVertex = ConfigurationVertex.findById(traversalSource, configId);
        if (configurationVertex == null)
            throw new IllegalArgumentException("Configuration with id " + configId + " does not exist");

        configurationVertex.addLanguage(languageVertex);

        return createConfigModel(languageId, configurationVertex);
    }

    @Override
    public void detachLanguageConfig(String languageId, String configId) {
        var languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        var configurationVertex = ConfigurationVertex.findById(traversalSource, configId);
        if (configurationVertex == null)
            throw new IllegalArgumentException("Configuration with id " + configId + " does not exist");

        configurationVertex.removeLanguage(languageVertex);
    }

    private static FlashcardLanguageDto createLanguageModel(LanguageVertex v) {
        var dto = new FlashcardLanguageDto();
        dto.setId(v.getId());
        dto.setName(v.getName());
        dto.setIsoCode(v.getIsoCode());
        return dto;
    }

    private static ConfigurationDto createConfigModel(String languageId, ConfigurationVertex vertex) {
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH))
            return createGoogleTextToSpeechConfigModel(languageId, vertex);
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TRANSLATE))
            return createGoogleTranslateConfigModel(languageId, vertex);

        // TODO SpeechToTextConfigurationModel mapping here// TODO SpeechToTextConfigurationModel mapping here
        throw new IllegalArgumentException("Unsupported configuration type: " + vertex.getType());
    }

    private static ConfigurationGoogleTextToSpeechDto createGoogleTextToSpeechConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationGoogleTextToSpeechDto();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(vertex.getPropertyValue("languageCode"));
        model.setVoiceName(vertex.getPropertyValue("voiceName"));
        return model;
    }

    private static ConfigurationGoogleTranslateDto createGoogleTranslateConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationGoogleTranslateDto();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(vertex.getPropertyValue("languageCode"));
        return model;
    }

    private static void updateConfigVertex(ConfigurationVertex vertex, ConfigurationDto model) {
        if (model instanceof ConfigurationGoogleTextToSpeechDto googleTextToSpeechModel) {
            updateGoogleTextToSpeechConfigVertex(vertex, googleTextToSpeechModel);
            return;
        }
        if (model instanceof ConfigurationGoogleTranslateDto googleTranslateModel) {
            updateGoogleTranslateConfigVertex(vertex, googleTranslateModel);
            return;
        }

        // TODO SpeechToTextConfigurationModel mapping here
        throw new IllegalArgumentException("Unsupported configuration model type: " + model.getClass().getName());
    }

    private static void updateGoogleTextToSpeechConfigVertex(ConfigurationVertex vertex, ConfigurationGoogleTextToSpeechDto model) {
        vertex.setType(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        vertex.setPropertyValue("languageCode", model.getLanguageCode());
        vertex.setPropertyValue("voiceName", model.getVoiceName());
    }

    private static void updateGoogleTranslateConfigVertex(ConfigurationVertex vertex, ConfigurationGoogleTranslateDto model) {
        vertex.setType(ConfigurationTypes.GOOGLE_TRANSLATE);
        vertex.setPropertyValue("languageCode", model.getLanguageCode());
    }
}
