package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.FlashcardDto;

import java.util.List;

public interface FlashcardRepository {
    FlashcardDto add(FlashcardDto FlashcardDto);

    FlashcardDto get(String id);

    List<FlashcardDto> getAll(String deckId);

    FlashcardDto update(FlashcardDto FlashcardDto);

    void delete(String id);
}
