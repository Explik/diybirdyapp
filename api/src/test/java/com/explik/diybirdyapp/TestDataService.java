package com.explik.diybirdyapp;

import com.explik.diybirdyapp.model.content.FlashcardDeckDto;
import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.service.FlashcardDeckService;
import com.explik.diybirdyapp.service.LanguageService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataService {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    LanguageService languageService;

    @Autowired
    FlashcardDeckService flashcardDeckService;

    public static final String LEFT_LANGUAGE_ID = "language1";
    public static final String RIGHT_LANGUAGE_ID = "language2";

    public static final String FLASHCARD_DECK_1_ID = "deck1";

    public void clear() {
        traversalSource.V().drop().iterate();
    }

    public void populateFlashcardLanguages() {
        var languageModel1 = new FlashcardLanguageDto();
        languageModel1.setId(LEFT_LANGUAGE_ID);
        languageModel1.setName("Language 1");
        languageModel1.setIsoCode("Lang 1");

        var languageModel2 = new FlashcardLanguageDto();
        languageModel2.setId(RIGHT_LANGUAGE_ID);
        languageModel2.setName("Language 2");
        languageModel2.setIsoCode("Lang 2");

        languageService.create(languageModel1);
        languageService.create(languageModel2);
    }

    public void populateFlashcardDecks() {
        var flashcardDeckModel = new FlashcardDeckDto();
        flashcardDeckModel.setId(FLASHCARD_DECK_1_ID);
        flashcardDeckModel.setName("Deck 1");

        flashcardDeckService.add(null, flashcardDeckModel);
    }
}
