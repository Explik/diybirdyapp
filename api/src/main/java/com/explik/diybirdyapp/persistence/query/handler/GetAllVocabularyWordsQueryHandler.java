package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.content.VocabularyContentTextDto;
import com.explik.diybirdyapp.persistence.query.GetAllVocabularyWordsQuery;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.WordVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllVocabularyWordsQueryHandler implements QueryHandler<GetAllVocabularyWordsQuery, List<VocabularyContentTextDto>> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public List<VocabularyContentTextDto> handle(GetAllVocabularyWordsQuery query) {
        var wordVertices = WordVertex.getAll(traversalSource);

        return wordVertices
            .stream()
            .map(GetAllVocabularyWordsQueryHandler::createFromMainExample)
            .toList();
    }

    private static VocabularyContentTextDto createFromMainExample(WordVertex v) {
        var buffer = new VocabularyContentTextDto();
        buffer.setText(v.getTextContent().getValue());

        var pronunciationVertex = PronunciationVertex.findByTextContentId(
                v.getUnderlyingSource(),
                v.getTextContent().getId());
        if (pronunciationVertex != null)
            buffer.setPronunciationUrl(pronunciationVertex.getAudioContent().getUrl());


        return buffer;
    }
}
