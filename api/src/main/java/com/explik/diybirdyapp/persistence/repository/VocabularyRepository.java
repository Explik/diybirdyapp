package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.VocabularyTextContentModel;

import java.util.List;

public interface VocabularyRepository {
    List<VocabularyTextContentModel> getAllWords();
}
