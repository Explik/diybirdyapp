package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.content.FlashcardDeckDto;
import com.explik.diybirdyapp.persistence.query.GetFlashcardDeckQuery;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetFlashcardDeckQueryHandler implements QueryHandler<GetFlashcardDeckQuery, FlashcardDeckDto> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public FlashcardDeckDto handle(GetFlashcardDeckQuery query) {
        var queryTraversal = traversalSource.V().has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, query.getDeckId());

        if (!queryTraversal.hasNext())
            throw new IllegalArgumentException("Flashcard with id " + query.getDeckId() + " not found");

        var vertex = new FlashcardDeckVertex(traversalSource, queryTraversal.next());
        if (!isDeckOwner(query.getUserId(), vertex))
            throw new IllegalArgumentException("FlashcardDeck with id " + query.getDeckId() + " not found");

        return createModel(vertex);
    }

    private static FlashcardDeckDto createModel(FlashcardDeckVertex v) {
        var model = new FlashcardDeckDto();
        model.setId(v.getId());
        model.setName(v.getName());
        model.setDescription(v.getDescription());
        return model;
    }

    private static boolean isDeckOwner(String userId, FlashcardDeckVertex v) {
        if (v.getOwner() == null)
            return true; // public deck

        return v.getOwner().getEmail().equals(userId);
    }
}
