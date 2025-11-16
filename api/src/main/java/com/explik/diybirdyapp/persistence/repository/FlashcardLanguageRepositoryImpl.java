package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleTextToSpeechModel;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleTranslateModel;
import com.explik.diybirdyapp.model.admin.ConfigurationModel;
import com.explik.diybirdyapp.model.content.FlashcardLanguageModel;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlashcardLanguageRepositoryImpl implements LanguageRepository {
    private final GraphTraversalSource traversalSource;

    public FlashcardLanguageRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardLanguageModel add(FlashcardLanguageModel language) {
        // Check for duplicates
        if (LanguageVertex.findById(traversalSource, language.getId()) != null)
            throw new IllegalArgumentException("Language with id " + language.getId() + " already exists");
        if (LanguageVertex.findByName(traversalSource, language.getName()) != null)
            throw new IllegalArgumentException("Language with name " + language.getName() + " already exists");
        if (LanguageVertex.findByAbbreviation(traversalSource, language.getAbbreviation()) != null)
            throw new IllegalArgumentException("Language with abbreviation " + language.getAbbreviation() + " already exists");

        // Add language to database
        var vertex = LanguageVertex.create(traversalSource);
        vertex.setId(language.getId());
        vertex.setName(language.getName());
        vertex.setAbbreviation(language.getAbbreviation());

        return createLanguageModel(vertex);
    }

    @Override
    public FlashcardLanguageModel getById(String languageId) {
        var vertex = LanguageVertex.findById(traversalSource, languageId);
        if (vertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        return createLanguageModel(vertex);
    }

    @Override
    public FlashcardLanguageModel update(FlashcardLanguageModel language) {
        var vertex = LanguageVertex.findById(traversalSource, language.getId());
        if (vertex == null)
            throw new IllegalArgumentException("Language with id " + language.getId() + " does not exist");

        if (language.getName() != null)
            vertex.setName(language.getName());
        if (language.getAbbreviation() != null)
            vertex.setAbbreviation(language.getAbbreviation());

        return createLanguageModel(vertex);
    }

    @Override
    public List<FlashcardLanguageModel> getAll() {
        var vertices = LanguageVertex.findAll(traversalSource);

        return vertices
            .stream()
            .map(FlashcardLanguageRepositoryImpl::createLanguageModel)
            .toList();
    }

    @Override
    public List<ConfigurationModel> getLanguageConfigs(String languageId, String configurationType) {
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
    public ConfigurationModel createLanguageConfig(String languageId, ConfigurationModel configModel) {
        var languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null)
            throw new IllegalArgumentException("Language with id " + languageId + " does not exist");

        var configurationVertex = ConfigurationVertex.create(traversalSource);
        updateConfigVertex(configurationVertex, configModel);
        configurationVertex.addLanguage(languageVertex);

        return createConfigModel(languageId, configurationVertex);
    }

    @Override
    public ConfigurationModel attachLanguageConfig(String languageId, String configId) {
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

    private static FlashcardLanguageModel createLanguageModel(LanguageVertex v) {
        return new FlashcardLanguageModel(
            v.getId(),
            v.getAbbreviation(),
            v.getName());
    }

    private static ConfigurationModel createConfigModel(String languageId, ConfigurationVertex vertex) {
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH))
            return createGoogleTextToSpeechConfigModel(languageId, vertex);
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TRANSLATE))
            return createGoogleTranslateConfigModel(languageId, vertex);

        // TODO SpeechToTextConfigurationModel mapping here// TODO SpeechToTextConfigurationModel mapping here
        throw new IllegalArgumentException("Unsupported configuration type: " + vertex.getType());
    }

    private static ConfigurationGoogleTextToSpeechModel createGoogleTextToSpeechConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationGoogleTextToSpeechModel();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(vertex.getPropertyValue("languageCode"));
        model.setVoiceName(vertex.getPropertyValue("voiceName"));
        return model;
    }

    private static ConfigurationGoogleTranslateModel createGoogleTranslateConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationGoogleTranslateModel();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(vertex.getPropertyValue("languageCode"));
        return model;
    }

    private static void updateConfigVertex(ConfigurationVertex vertex, ConfigurationModel model) {
        if (model instanceof ConfigurationGoogleTextToSpeechModel googleTextToSpeechModel) {
            updateGoogleTextToSpeechConfigVertex(vertex, googleTextToSpeechModel);
            return;
        }
        if (model instanceof ConfigurationGoogleTranslateModel googleTranslateModel) {
            updateGoogleTranslateConfigVertex(vertex, googleTranslateModel);
            return;
        }

        // TODO SpeechToTextConfigurationModel mapping here
        throw new IllegalArgumentException("Unsupported configuration model type: " + model.getClass().getName());
    }

    private static void updateGoogleTextToSpeechConfigVertex(ConfigurationVertex vertex, ConfigurationGoogleTextToSpeechModel model) {
        vertex.setType(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        vertex.setPropertyValue("languageCode", model.getLanguageCode());
        vertex.setPropertyValue("voiceName", model.getVoiceName());
    }

    private static void updateGoogleTranslateConfigVertex(ConfigurationVertex vertex, ConfigurationGoogleTranslateModel model) {
        vertex.setType(ConfigurationTypes.GOOGLE_TRANSLATE);
        vertex.setPropertyValue("languageCode", model.getLanguageCode());
    }
}
