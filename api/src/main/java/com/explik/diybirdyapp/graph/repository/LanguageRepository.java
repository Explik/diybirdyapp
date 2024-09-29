package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import java.util.List;

public interface LanguageRepository {
    FlashcardLanguageModel add(FlashcardLanguageModel language);
    List<FlashcardLanguageModel> getAll();
}
