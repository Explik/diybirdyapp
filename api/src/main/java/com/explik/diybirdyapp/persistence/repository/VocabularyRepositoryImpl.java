package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.VocabularyTextContentModel;
import com.explik.diybirdyapp.persistence.vertex.WordVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VocabularyRepositoryImpl implements VocabularyRepository {
    private final GraphTraversalSource traversalSource;

    public VocabularyRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public List<VocabularyTextContentModel> getAllWords() {
        var wordVertices = WordVertex.getAll(traversalSource);




        return wordVertices
            .stream()
            .map(VocabularyRepositoryImpl::createFromMainExample)
            .toList();
    }

    private static VocabularyTextContentModel createFromMainExample(WordVertex v) {
        var mainExample = v.getMainExample();

        return new VocabularyTextContentModel(mainExample.getValue());
    }
}
