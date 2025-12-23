package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.content.FlashcardDto;
import com.explik.diybirdyapp.persistence.query.modelFactory.FlashcardModelFactory;
import com.explik.diybirdyapp.persistence.query.GetAllFlashcardsQuery;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllFlashcardsQueryHandler implements QueryHandler<GetAllFlashcardsQuery, List<FlashcardDto>> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    FlashcardModelFactory flashcardCardModelFactory;

    @Override
    public List<FlashcardDto> handle(GetAllFlashcardsQuery query) {
        List<FlashcardVertex> vertices;

        if (query.getDeckId() != null) {
            vertices = FlashcardVertex.findByDeckId(traversalSource, query.getDeckId());
        }
        else {
            vertices = FlashcardVertex.findAll(traversalSource);
        }

        return vertices
            .stream()
            .map(v -> flashcardCardModelFactory.create(v))
            .toList();
    }
}
