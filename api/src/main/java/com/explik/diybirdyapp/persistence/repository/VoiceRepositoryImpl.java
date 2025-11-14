package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.content.VoiceModel;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VoiceRepositoryImpl implements VoiceRepository {
    private final GraphTraversalSource traversalSource;

    public VoiceRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public VoiceModel get(String languageId) {
        var languageVertex = LanguageVertex.findById(traversalSource, languageId);
        if (languageVertex == null)
            return null;

        var voiceConfigs = ConfigurationVertex.findByLanguageAndType(languageVertex, ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        var voiceConfig = voiceConfigs.stream().findFirst().orElse(null);
        if (voiceConfig == null)
            return null;

        return createModel(languageVertex, voiceConfig);
    }

    private static VoiceModel createModel(LanguageVertex langVertex, ConfigurationVertex configVertex) {
        var model = new VoiceModel();
        model.setLanguageId(langVertex.getId());
        model.setLanguageName(langVertex.getName());
        model.setVoiceId(configVertex.getId());
        model.setVoiceName(configVertex.getProperty("voiceName"));
        model.setVoiceLanguageCode(configVertex.getProperty("languageCode"));
        return model;
    }
}
