package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.content.FlashcardDeckDto;
import com.explik.diybirdyapp.persistence.query.GetAllFlashcardDecksQuery;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllFlashcardDecksQueryHandler implements QueryHandler<GetAllFlashcardDecksQuery, List<FlashcardDeckDto>> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public List<FlashcardDeckDto> handle(GetAllFlashcardDecksQuery query) {
        var vertices = traversalSource.V().hasLabel(FlashcardDeckVertex.LABEL).toList();

        return vertices
            .stream()
            .map(v -> new FlashcardDeckVertex(traversalSource, v))
                .filter(deck -> isDeckOwner(query.getUserId(), deck))
                .map(GetAllFlashcardDecksQueryHandler::createModel)
            .toList();
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
