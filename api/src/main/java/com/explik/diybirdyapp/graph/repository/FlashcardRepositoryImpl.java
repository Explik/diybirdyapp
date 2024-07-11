package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.explik.diybirdyapp.graph.model.LanguageModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlashcardRepositoryImpl implements FlashcardRepository {
    private final List<FlashcardModel> flashcardModels;

    public FlashcardRepositoryImpl() {
        LanguageModel lang1 = new LanguageModel("DA", "Danish");
        LanguageModel lang2 = new LanguageModel("EN", "English");

        flashcardModels = new ArrayList<>();
        flashcardModels.add(new FlashcardModel("Hej verden", lang1, "Hello world", lang2));
        flashcardModels.add(new FlashcardModel("Hej John", lang1, "Hello John", lang2));
    }

    @Override
    public void add(FlashcardModel flashcardModel) {
        flashcardModels.add(flashcardModel);
    }

    @Override
    public FlashcardModel get(String id) {
        return null;
    }

    @Override
    public List<FlashcardModel> getAll() {
        return flashcardModels;
    }
}
