package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
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
    public FlashcardLanguageModel add(FlashcardLanguageModel language) {
        // Check for duplicates
        if (LanguageVertex.findById(traversalSource, language.getId()) != null)
            throw new IllegalArgumentException("Language with id " + language.getId() + " already exists");
        if (LanguageVertex.findByName(traversalSource, language.getName()) != null)
            throw new IllegalArgumentException("Language with name " + language.getName() + " already exists");
        if (LanguageVertex.findByAbbreviation(traversalSource, language.getAbbreviation()) != null)
            throw new IllegalArgumentException("Language with abbreviation " + language.getAbbreviation() + " already exists");

        // Add language to database
        var vertex = LanguageVertex.create(traversalSource);
        vertex.setId(language.getId());
        vertex.setName(language.getName());
        vertex.setAbbreviation(language.getAbbreviation());

        return create(vertex);
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
