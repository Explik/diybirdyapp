package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.graph.vertex.LanguageVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageRepositoryImpl implements LanguageRepository {
    private final GraphTraversalSource traversalSource;

    public LanguageRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public List<FlashcardLanguageModel> getAll() {
        var vertices = LanguageVertex.findAll(traversalSource);

        return vertices
            .stream()
            .map(LanguageRepositoryImpl::create)
            .toList();
    }

    private static FlashcardLanguageModel create(LanguageVertex v) {
        return new FlashcardLanguageModel(
            v.getId(),
            v.getAbbreviation(),
            v.getName());
    }
}
