package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.query.GetLanguageByIdQuery;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetLanguageByIdQueryHandler implements QueryHandler<GetLanguageByIdQuery, FlashcardLanguageDto> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public FlashcardLanguageDto handle(GetLanguageByIdQuery query) {
        var vertex = LanguageVertex.findById(traversalSource, query.getLanguageId());
        if (vertex == null)
            throw new IllegalArgumentException("Language with id " + query.getLanguageId() + " does not exist");

        return createLanguageModel(vertex);
    }

    private static FlashcardLanguageDto createLanguageModel(LanguageVertex v) {
        var dto = new FlashcardLanguageDto();
        dto.setId(v.getId());
        dto.setName(v.getName());
        dto.setIsoCode(v.getIsoCode());
        return dto;
    }
}
