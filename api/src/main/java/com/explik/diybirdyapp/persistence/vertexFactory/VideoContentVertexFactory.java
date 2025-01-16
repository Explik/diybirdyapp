package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.VideoContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VideoContentVertexFactory implements ContentVertexFactory<VideoContentVertex, VideoContentVertexFactory.Options> {
    @Override
    public VideoContentVertex create(GraphTraversalSource traversalSource, Options options) {
        var vertex = VideoContentVertex.create(traversalSource);
        vertex.setId(options.id);
        vertex.setUrl(options.url);
        vertex.setLanguage(options.languageVertex);

        return vertex;
    }

    @Override
    public VideoContentVertex copy(VideoContentVertex existingVertex) {
        var traversalSource = existingVertex.getUnderlyingSource();
        var vertex = VideoContentVertex.create(traversalSource);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setUrl(existingVertex.getUrl());
        vertex.setLanguage(existingVertex.getLanguage());

        return vertex;
    }

    public record Options (String id, String url, LanguageVertex languageVertex) {}
}
