package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.FlashcardDeckModel;

import java.util.List;

public interface FlashcardDeckRepository {
    FlashcardDeckModel add(String userId, FlashcardDeckModel flashcardDeckModel);

    FlashcardDeckModel get(String userId, String id);

    FlashcardDeckModel update(String userId, FlashcardDeckModel flashcardDeckModel);

    List<FlashcardDeckModel> getAll(String userId);

    void delete(String userId, String id);
}
