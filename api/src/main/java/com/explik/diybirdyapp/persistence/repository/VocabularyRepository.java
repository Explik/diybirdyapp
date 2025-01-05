package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.VocabularyTextContentModel;

import java.util.List;

public interface VocabularyRepository {
    List<VocabularyTextContentModel> getAllWords();
}
