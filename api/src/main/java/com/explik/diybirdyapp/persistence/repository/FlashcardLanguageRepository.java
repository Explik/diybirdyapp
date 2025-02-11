package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.FlashcardLanguageModel;
import java.util.List;

public interface FlashcardLanguageRepository {
    FlashcardLanguageModel add(FlashcardLanguageModel language);
    List<FlashcardLanguageModel> getAll();
}
