package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleSpeechToTextDto;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleTextToSpeechDto;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleTranslateDto;
import com.explik.diybirdyapp.persistence.query.GetConfigurationByIdQuery;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetConfigurationByIdQueryHandler implements QueryHandler<GetConfigurationByIdQuery, ConfigurationDto> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public ConfigurationDto handle(GetConfigurationByIdQuery query) {
        var configurationVertex = ConfigurationVertex.findById(traversalSource, query.getConfigId());
        if (configurationVertex == null)
            throw new IllegalArgumentException("Configuration with id " + query.getConfigId() + " does not exist");

        return createConfigModel(null, configurationVertex);
    }

    private static ConfigurationDto createConfigModel(String languageId, ConfigurationVertex vertex) {
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH))
            return createGoogleTextToSpeechConfigModel(languageId, vertex);
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TRANSLATE))
            return createGoogleTranslateConfigModel(languageId, vertex);
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT))
            return createGoogleSpeechToTextConfigModel(languageId, vertex);

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
}
