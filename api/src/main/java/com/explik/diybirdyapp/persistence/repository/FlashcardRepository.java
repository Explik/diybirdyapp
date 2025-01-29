package com.explik.diybirdyapp.persistence.repository;

import java.util.List;
import com.explik.diybirdyapp.model.content.FlashcardModel;

public interface FlashcardRepository {
    FlashcardModel add(FlashcardModel flashcardModel);

    FlashcardModel get(String id);

    List<FlashcardModel> getAll(String deckId);

    FlashcardModel update(FlashcardModel flashcardModel);

    void delete(String id);
}
