package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardDeckModel;

import java.util.List;

public interface FlashcardDeckRepository {
    FlashcardDeckModel get(String id);

    FlashcardDeckModel update(FlashcardDeckModel flashcardDeckModel);

    List<FlashcardDeckModel> getAll();
}
