package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.VoiceModel;
import com.explik.diybirdyapp.persistence.vertex.TextToSpeechConfigVertex;
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
        var configs = TextToSpeechConfigVertex.findByLanguageId(traversalSource, languageId);
        var config = configs.stream().findFirst().orElse(null); // The first language is arbitrarily chosen

        if (config == null)
            return null;

        return createModel(config);
    }

    private static VoiceModel createModel(TextToSpeechConfigVertex config) {
        var configLanguage = config.getLanguage();

        var model = new VoiceModel();
        model.setLanguageId(configLanguage.getId());
        model.setLanguageName(configLanguage.getName());
        model.setVoiceId(config.getId());
        model.setVoiceName(config.getVoiceName());
        model.setVoiceLanguageCode(config.getLanguageCode());
        return model;
    }
}
