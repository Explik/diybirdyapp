package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.persistence.query.GetAllLanguagesQuery;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllLanguagesQueryHandler implements QueryHandler<GetAllLanguagesQuery, List<FlashcardLanguageDto>> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public List<FlashcardLanguageDto> handle(GetAllLanguagesQuery query) {
        var vertices = LanguageVertex.findAll(traversalSource);

        return vertices
            .stream()
            .map(GetAllLanguagesQueryHandler::createLanguageModel)
            .toList();
    }

    private static FlashcardLanguageDto createLanguageModel(LanguageVertex v) {
        var dto = new FlashcardLanguageDto();
        dto.setId(v.getId());
        dto.setName(v.getName());
        dto.setIsoCode(v.getIsoCode());
        return dto;
    }
}
