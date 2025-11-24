package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.VocabularyContentTextDto;

import java.util.List;

public interface VocabularyRepository {
    List<VocabularyContentTextDto> getAllWords();
}
