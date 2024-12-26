package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.FlashcardDeckModel;

import java.util.List;

public interface FlashcardDeckRepository {
    FlashcardDeckModel add(FlashcardDeckModel flashcardDeckModel);

    FlashcardDeckModel get(String id);

    FlashcardDeckModel update(FlashcardDeckModel flashcardDeckModel);

    List<FlashcardDeckModel> getAll();
}
