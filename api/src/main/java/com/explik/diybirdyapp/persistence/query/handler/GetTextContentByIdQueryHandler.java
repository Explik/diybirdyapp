package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.persistence.query.GetTextContentByIdQuery;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Query handler for retrieving a text content vertex by ID.
 */
@Component
public class GetTextContentByIdQueryHandler implements QueryHandler<GetTextContentByIdQuery, TextContentVertex> {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Override
    public TextContentVertex handle(GetTextContentByIdQuery query) {
        return TextContentVertex.findById(traversalSource, query.getId());
    }
}
