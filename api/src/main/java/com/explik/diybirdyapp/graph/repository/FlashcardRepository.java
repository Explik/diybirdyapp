package com.explik.diybirdyapp.graph.repository;

import java.util.List;
import com.explik.diybirdyapp.graph.model.FlashcardModel;

public interface FlashcardRepository {
    void add(FlashcardModel flashcardModel);

    FlashcardModel get(String id);

    List<FlashcardModel> getAll();
}