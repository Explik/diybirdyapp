package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.content.VocabularyDto;
import com.explik.diybirdyapp.persistence.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VocabularyService {
    @Autowired
    VocabularyRepository repository;

    public VocabularyDto get() {
        var vocabulary = new VocabularyDto();

        var vocabularyWords = repository.getAllWords();
        vocabulary.setWords(vocabularyWords);

        return vocabulary;
    }
}
