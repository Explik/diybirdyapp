package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.VocabularyContentTextDto;
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
    public List<VocabularyContentTextDto> getAllWords() {
        var wordVertices = WordVertex.getAll(traversalSource);

        return wordVertices
            .stream()
            .map(VocabularyRepositoryImpl::createFromMainExample)
            .toList();
    }

    private static VocabularyContentTextDto createFromMainExample(WordVertex v) {
        var buffer = new VocabularyContentTextDto();
        buffer.setText(v.getTextContent().getValue());

        if (v.getTextContent().getMainPronunciation() != null) {
            buffer.setPronunciationUrl(v.getTextContent().getMainPronunciation().getAudioContent().getUrl());
        }
        return buffer;
    }
}
