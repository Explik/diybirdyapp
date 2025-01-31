package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AudioContentVertexFactory implements ContentVertexFactory<AudioContentVertex, AudioContentVertexFactory.Options> {
    @Override
    public AudioContentVertex create(GraphTraversalSource traversalSource, Options options) {
        var vertex = AudioContentVertex.create(traversalSource);
        vertex.setId(options.id);
        vertex.setUrl(options.url);
        vertex.setLanguage(options.languageVertex);

        return vertex;
    }

    @Override
    public AudioContentVertex copy(AudioContentVertex existingVertex) {
        var traversalSource = existingVertex.getUnderlyingSource();
        var vertex = AudioContentVertex.create(traversalSource);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setUrl(existingVertex.getUrl());
        vertex.setLanguage(existingVertex.getLanguage());

        return vertex;
    }

    public record Options (String id, String url, LanguageVertex languageVertex) {}
}
