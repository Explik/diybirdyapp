package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.content.FlashcardDto;
import com.explik.diybirdyapp.persistence.modelFactory.FlashcardModelFactory;
import com.explik.diybirdyapp.persistence.query.GetFlashcardByIdQuery;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetFlashcardByIdQueryHandler implements QueryHandler<GetFlashcardByIdQuery, FlashcardDto> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    FlashcardModelFactory flashcardCardModelFactory;

    @Override
    public FlashcardDto handle(GetFlashcardByIdQuery query) {
        var vertex = FlashcardVertex.findById(traversalSource, query.getId());
        return flashcardCardModelFactory.create(vertex);
    }
}
