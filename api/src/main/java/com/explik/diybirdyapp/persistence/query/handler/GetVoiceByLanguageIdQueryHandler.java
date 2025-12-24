package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.internal.GoogleTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.MicrosoftTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.VoiceModel;
import com.explik.diybirdyapp.persistence.query.GetVoiceByLanguageIdQuery;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetVoiceByLanguageIdQueryHandler implements QueryHandler<GetVoiceByLanguageIdQuery, VoiceModel> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public VoiceModel handle(GetVoiceByLanguageIdQuery query) {
        var languageVertex = LanguageVertex.findById(traversalSource, query.getLanguageId());
        if (languageVertex == null)
            return null;

        // Try to get Google TTS configuration first, then Microsoft TTS
        ConfigurationVertex configurationVertex = null;

        if(configurationVertex == null)
            configurationVertex = getConfiguration(languageVertex, ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        if (configurationVertex == null)
            configurationVertex = getConfiguration(languageVertex, ConfigurationTypes.MICROSOFT_TEXT_TO_SPEECH);

        if (configurationVertex == null)
            return null;

        return createModel(languageVertex, configurationVertex);
    }

    private static ConfigurationVertex getConfiguration(LanguageVertex languageVertex, String configurationType) {
        return ConfigurationVertex
                .findByLanguageAndType(languageVertex, configurationType)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private static VoiceModel createModel(LanguageVertex langVertex, ConfigurationVertex configVertex) {
        return switch (configVertex.getType()) {
            case ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH -> createGoogleModel(langVertex, configVertex);
            case ConfigurationTypes.MICROSOFT_TEXT_TO_SPEECH -> createMicrosoftModel(langVertex, configVertex);
            default -> throw new IllegalArgumentException("Unsupported configuration type: " + configVertex.getType());
        };
    }

    private static GoogleTextToSpeechVoiceModel createGoogleModel(LanguageVertex langVertex, ConfigurationVertex configVertex) {
        var model = new GoogleTextToSpeechVoiceModel();
        model.setLanguageId(langVertex.getId());
        model.setLanguageName(langVertex.getName());
        model.setVoiceId(configVertex.getId());
        model.setVoiceName(configVertex.getPropertyValue("voiceName"));
        model.setVoiceLanguageCode(configVertex.getPropertyValue("languageCode"));
        return model;
    }

    private static MicrosoftTextToSpeechVoiceModel createMicrosoftModel(LanguageVertex langVertex, ConfigurationVertex configVertex) {
        var model = new MicrosoftTextToSpeechVoiceModel();
        model.setLanguageId(langVertex.getId());
        model.setLanguageName(langVertex.getName());
        model.setVoiceName(configVertex.getPropertyValue("voiceName"));
        return model;
    }
}
