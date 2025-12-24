package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.content.VocabularyDto;
import com.explik.diybirdyapp.persistence.query.GetAllVocabularyWordsQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.model.content.VocabularyContentTextDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VocabularyService {
    @Autowired
    private QueryHandler<GetAllVocabularyWordsQuery, List<VocabularyContentTextDto>> getAllVocabularyWordsQueryHandler;

    public VocabularyDto get() {
        var vocabulary = new VocabularyDto();

        var query = new GetAllVocabularyWordsQuery();
        var vocabularyWords = getAllVocabularyWordsQueryHandler.handle(query);
        vocabulary.setWords(vocabularyWords);

        return vocabulary;
    }
}
