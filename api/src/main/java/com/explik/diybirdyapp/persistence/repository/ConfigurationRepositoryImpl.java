package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.admin.ConfigurationModel;
import com.explik.diybirdyapp.persistence.vertex.TextToSpeechConfigVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigurationRepositoryImpl implements ConfigurationRepository {
    private final GraphTraversalSource traversalSource;

    public ConfigurationRepositoryImpl(@Autowired  GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public List<ConfigurationModel> getAll(String languageId) {
        var configs = TextToSpeechConfigVertex.findByLanguageId(traversalSource, languageId);

        return configs
            .stream()
            .map(v -> create(v, languageId))
            .toList();
    }

    private static ConfigurationModel create(TextToSpeechConfigVertex v, String languageId) {
        var model = new ConfigurationModel();
        model.setId(v.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(v.getLanguageCode());

        return model;
    }
}
