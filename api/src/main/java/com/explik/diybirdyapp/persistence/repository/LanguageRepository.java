package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.FlashcardLanguageModel;
import java.util.List;

public interface LanguageRepository {
    FlashcardLanguageModel add(FlashcardLanguageModel language);
    List<FlashcardLanguageModel> getAll();
}
