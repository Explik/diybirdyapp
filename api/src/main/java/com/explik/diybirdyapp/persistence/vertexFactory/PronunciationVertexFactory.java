package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

@Component
public class PronunciationVertexFactory implements VertexFactory<PronunciationVertex, PronunciationVertexFactory.Options> {
    @Override
    public PronunciationVertex create(GraphTraversalSource traversalSource, Options options) {
        var vertex = PronunciationVertex.create(traversalSource);
        vertex.setId(options.id);
        vertex.setAudioContent(options.audioContentVertex);

        options.sourceVertex.addPronunciation(vertex);
        if (!options.sourceVertex.hasMainPronunciation())
            options.sourceVertex.setMainPronunciation(vertex);
        options.sourceVertex.makeStatic();

        return vertex;
    }

    public record Options (String id, TextContentVertex sourceVertex, AudioContentVertex audioContentVertex) { }
}
