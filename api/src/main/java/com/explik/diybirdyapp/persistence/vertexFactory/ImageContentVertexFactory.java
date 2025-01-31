package com.explik.diybirdyapp.persistence.vertexFactory;

import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ImageContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ImageContentVertexFactory implements ContentVertexFactory<ImageContentVertex, ImageContentVertexFactory.Options> {
    @Override
    public ImageContentVertex create(GraphTraversalSource traversalSource, Options options) {
        var vertex = ImageContentVertex.create(traversalSource);
        vertex.setId(options.id());
        vertex.setUrl(options.url());

        return vertex;
    }

    @Override
    public ImageContentVertex copy(ImageContentVertex existingVertex) {
        var traversalSource = existingVertex.getUnderlyingSource();
        var vertex = ImageContentVertex.create(traversalSource);
        vertex.setId(UUID.randomUUID().toString());
        vertex.setUrl(existingVertex.getUrl());
        return vertex;
    }

    public record Options (String id, String url) { }
}
