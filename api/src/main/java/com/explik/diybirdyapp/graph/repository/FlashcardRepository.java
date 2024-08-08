package com.explik.diybirdyapp.graph.repository;

import java.util.List;
import com.explik.diybirdyapp.graph.model.FlashcardModel;

public interface FlashcardRepository {
    FlashcardModel add(FlashcardModel flashcardModel);

    FlashcardModel get(String id);

    List<FlashcardModel> getAll(String deckId);

    FlashcardModel update(FlashcardModel flashcardModel);
}
