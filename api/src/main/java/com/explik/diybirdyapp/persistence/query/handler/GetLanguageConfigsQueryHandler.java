package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.*;
import com.explik.diybirdyapp.persistence.query.GetLanguageConfigsQuery;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetLanguageConfigsQueryHandler implements QueryHandler<GetLanguageConfigsQuery, List<ConfigurationDto>> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public List<ConfigurationDto> handle(GetLanguageConfigsQuery query) {
        var languageVertex = LanguageVertex.findById(traversalSource, query.getLanguageId());
        if (languageVertex == null)
            throw new IllegalArgumentException("Language with id " + query.getLanguageId() + " does not exist");

        List<ConfigurationVertex> configurationVertices;
        if (query.getConfigurationType() == null) {
            configurationVertices = ConfigurationVertex.findByLanguage(languageVertex);
        } else {
            configurationVertices = ConfigurationVertex.findByLanguageAndType(languageVertex, query.getConfigurationType());
        }

        return configurationVertices
            .stream()
            .map(v -> createConfigModel(query.getLanguageId(), v))
            .toList();
    }

    private static ConfigurationDto createConfigModel(String languageId, ConfigurationVertex vertex) {
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH))
            return createGoogleTextToSpeechConfigModel(languageId, vertex);
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TRANSLATE))
            return createGoogleTranslateConfigModel(languageId, vertex);
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT))
            return createGoogleSpeechToTextConfigModel(languageId, vertex);
        if (vertex.getType().equals(ConfigurationTypes.MICROSOFT_TEXT_TO_SPEECH))
            return createMicrosoftTextToSpeechConfigModel(languageId, vertex);

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

    private static ConfigurationGoogleSpeechToTextDto createGoogleSpeechToTextConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationGoogleSpeechToTextDto();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(vertex.getPropertyValue("languageCode"));
        return model;
    }

    private static ConfigurationGoogleTranslateDto createGoogleTranslateConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationGoogleTranslateDto();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(vertex.getPropertyValue("languageCode"));
        return model;
    }

    private static ConfigurationMicrosoftTextToSpeechDto createMicrosoftTextToSpeechConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationMicrosoftTextToSpeechDto();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setVoiceName(vertex.getPropertyValue("voiceName"));
        return model;
    }
}
