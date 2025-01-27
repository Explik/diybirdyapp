package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.content.VocabularyModel;
import com.explik.diybirdyapp.persistence.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VocabularyService {
    @Autowired
    VocabularyRepository repository;

    public VocabularyModel get() {
        var vocabulary = new VocabularyModel();

        var vocabularyWords = repository.getAllWords();
        vocabulary.setWords(vocabularyWords);

        return vocabulary;
    }
}
