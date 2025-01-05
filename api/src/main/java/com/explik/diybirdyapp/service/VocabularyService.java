package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.ExerciseContentTextModel;
import com.explik.diybirdyapp.model.VocabularyModel;
import com.explik.diybirdyapp.model.VocabularyTextContentModel;
import com.explik.diybirdyapp.persistence.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
