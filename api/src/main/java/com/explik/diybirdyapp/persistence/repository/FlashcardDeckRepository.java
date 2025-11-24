package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.FlashcardDeckDto;

import java.util.List;

public interface FlashcardDeckRepository {
    FlashcardDeckDto add(String userId, FlashcardDeckDto flashcardDeckModel);

    FlashcardDeckDto get(String userId, String id);

    FlashcardDeckDto update(String userId, FlashcardDeckDto flashcardDeckModel);

    List<FlashcardDeckDto> getAll(String userId);

    void delete(String userId, String id);
}
