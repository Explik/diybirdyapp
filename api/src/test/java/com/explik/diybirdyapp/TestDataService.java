package com.explik.diybirdyapp;

import com.explik.diybirdyapp.model.content.FlashcardDeckModel;
import com.explik.diybirdyapp.model.content.FlashcardLanguageModel;
import com.explik.diybirdyapp.persistence.repository.FlashcardDeckRepository;
import com.explik.diybirdyapp.persistence.repository.FlashcardLanguageRepository;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataService {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    FlashcardLanguageRepository languageRepository;

    @Autowired
    FlashcardDeckRepository flashcardDeckRepository;

    public static final String LEFT_LANGUAGE_ID = "language1";
    public static final String RIGHT_LANGUAGE_ID = "language2";

    public static final String FLASHCARD_DECK_1_ID = "deck1";

    public void clear() {
        traversalSource.V().drop().iterate();
    }

    public void populateFlashcardLanguages() {
        var languageModel1 = new FlashcardLanguageModel();
        languageModel1.setId(LEFT_LANGUAGE_ID);
        languageModel1.setName("Language 1");
        languageModel1.setAbbreviation("Lang 1");

        var languageModel2 = new FlashcardLanguageModel();
        languageModel2.setId(RIGHT_LANGUAGE_ID);
        languageModel2.setName("Language 2");
        languageModel2.setAbbreviation("Lang 2");

        languageRepository.add(languageModel1);
        languageRepository.add(languageModel2);
    }

    public void populateFlashcardDecks() {
        var flashcardDeckModel = new FlashcardDeckModel();
        flashcardDeckModel.setId(FLASHCARD_DECK_1_ID);
        flashcardDeckModel.setName("Deck 1");

        flashcardDeckRepository.add(flashcardDeckModel);
    }
}
