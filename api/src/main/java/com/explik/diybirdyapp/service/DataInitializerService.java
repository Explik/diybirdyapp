package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.service.TextToSpeechService;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperationsReviewFlashcardDeck;
import com.explik.diybirdyapp.persistence.vertexFactory.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializerService {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private ExerciseWriteSentenceUsingWordVertexFactory exerciseWriteSentenceUsingWordVertexFactory;

    @Autowired
    private ExerciseWriteTranslatedSentenceVertexFactory exerciseWriteTranslatedSentenceVertexFactory;

    @Autowired
    private ExerciseSelectFlashcardVertexFactory exerciseSelectFlashcardVertexFactory;

    @Autowired
    private ExercisePronounceFlashcardVertexFactory exercisePronounceFlashcardVertexFactory;

    @Autowired
    private ExerciseReviewFlashcardVertexFactory exerciseReviewFlashcardVertexFactory;

    @Autowired
    private ExerciseSessionOperationsReviewFlashcardDeck exerciseSessionFlashcardReviewVertexFactory;

    @Autowired
    private FlashcardVertexFactory flashcardVertexFactory;

    @Autowired
    private FlashcardDeckVertexFactory flashcardDeckVertexFactory;

    @Autowired
    private LanguageVertexFactory languageVertexFactory;

    @Autowired
    private TextContentVertexFactory textContentVertexFactory;

    @Autowired
    private TextToSpeechConfigVertexFactory textToSpeechConfigVertexFactory;

    @Autowired
    private WordVertexFactory wordVertexFactory;

    @Autowired
    private PronunciationVertexFactory pronunciationVertexFactory;

    @Autowired
    private AudioContentVertexFactory audioContentVertexFactory;

    public void resetInitialData() {
        traversalSource.V().drop().iterate();
        appendInitialData();
    }

    public void appendInitialData() {
        addInitialLanguageData();
        addInitialContentAndConcepts();
        addInitialExerciseData();
    }

    public void addInitialLanguageData() {
        var danish = languageVertexFactory.create(
                traversalSource,
                new LanguageVertexFactory.Options("langVertex1", "Danish", "DA"));

        var english = languageVertexFactory.create(
                traversalSource,
                new LanguageVertexFactory.Options("langVertex2", "English", "EN"));

        // Adding Google Text-to-Speech config for languages
        var danishTextToSpeechConfig = textToSpeechConfigVertexFactory.create(
                traversalSource,
                new TextToSpeechConfigVertexFactory.Options("danishTextToSpeechConfig", "da-DK", "da-DK-Wavenet-A", danish));

        var englishTextToSpeechConfig = textToSpeechConfigVertexFactory.create(
                traversalSource,
                new TextToSpeechConfigVertexFactory.Options("englishTextToSpeechConfig", "en-US", "en-US-Wavenet-A", english));
    }

    public void addInitialContentAndConcepts() {
        var langVertex1 = LanguageVertex.findByAbbreviation(traversalSource, "DA");
        var langVertex2 = LanguageVertex.findByAbbreviation(traversalSource, "EN");

        // Text content
        var textVertex0 = TextContentVertex.create(traversalSource);
        textVertex0.setId("textVertex0");
        textVertex0.setValue("Bereshit");
        textVertex0.setLanguage(langVertex1);

        var textVertex1 = TextContentVertex.create(traversalSource);
        textVertex1.setId("textVertex1");
        textVertex1.setValue("Hej verden");
        textVertex1.setLanguage(langVertex1);

        var textVertex2 = TextContentVertex.create(traversalSource);
        textVertex2.setId("textVertex2");
        textVertex2.setValue("Hello world");
        textVertex2.setLanguage(langVertex2);

        var textVertex3 = TextContentVertex.create(traversalSource);
        textVertex3.setId("textVertex3");
        textVertex3.setValue("Hej John");
        textVertex3.setLanguage(langVertex1);

        var textVertex4 = TextContentVertex.create(traversalSource);
        textVertex4.setId("textVertex4");
        textVertex4.setValue("Hey John");
        textVertex4.setLanguage(langVertex2);

        // Flashcard content
        var flashcardVertex1 = FlashcardVertex.create(traversalSource);
        flashcardVertex1.setId("flashcardVertex1");
        flashcardVertex1.setLeftContent(textVertex1);
        flashcardVertex1.setRightContent(textVertex2);

        var flashcardVertex2 = FlashcardVertex.create(traversalSource);
        flashcardVertex2.setId("flashcardVertex2");
        flashcardVertex2.setLeftContent(textVertex3);
        flashcardVertex2.setRightContent(textVertex4);

        var flashcardDeckVertex1 = FlashcardDeckVertex.create(traversalSource);
        flashcardDeckVertex1.setId("flashcardDeckVertex1");
        flashcardDeckVertex1.setName("First ever flashcard deck");
        flashcardDeckVertex1.addFlashcard(flashcardVertex1);

        var flashcardDeckVertex2 = FlashcardDeckVertex.create(traversalSource);
        flashcardDeckVertex2.setId("flashcardDeckVertex2");
        flashcardDeckVertex2.setName("Second ever flashcard deck");
        flashcardDeckVertex2.addFlashcard(flashcardVertex2);

        // Word concepts
        var wordVertex1 = WordVertex.create(traversalSource);
        wordVertex1.setId("wordVertex1");
        wordVertex1.setValue(textVertex0.getValue());
        wordVertex1.setLanguage(textVertex0.getLanguage());
        wordVertex1.addExample(textVertex0);
        wordVertex1.setMainExample(textVertex0);

        // Pronunciation concept
        var audioContentVertex1 = audioContentVertexFactory.create(
                traversalSource,
                new AudioContentVertexFactory.Options("audioContentVertex1", "https://example.com/audio1.mp3", langVertex1));
        var pronunciationVertex1 = pronunciationVertexFactory.create(
                traversalSource,
                new PronunciationVertexFactory.Options("pronunciationVertex1", textVertex0, audioContentVertex1));
    }

    public void addInitialExerciseData() {
        var langVertex = LanguageVertex.findByAbbreviation(traversalSource, "EN");

        var wordVertex1 = textContentVertexFactory.create(
                traversalSource,
                new TextContentVertexFactory.Options("1", "example", langVertex));

        exerciseWriteSentenceUsingWordVertexFactory.create(
                traversalSource,
                new ExerciseWriteSentenceUsingWordVertexFactory.Options("1", "example", wordVertex1));

        // Exercise 2 - Translate sentence to Danish
        var wordVertex2 = textContentVertexFactory.create(
                traversalSource,
                new TextContentVertexFactory.Options("2", "This is an example sentence", langVertex));

        exerciseWriteTranslatedSentenceVertexFactory.create(
                traversalSource,
                new ExerciseWriteTranslatedSentenceVertexFactory.Options("2", "Danish", wordVertex2));

        // Exercise 3 - Multiple choice text exercise
        var wordVertex3 = textContentVertexFactory.create(
                traversalSource,
                new TextContentVertexFactory.Options("3", "Random option 1", langVertex));

        var wordVertex4 = textContentVertexFactory.create(
                traversalSource,
                new TextContentVertexFactory.Options("4", "Random option 2", langVertex));

        var wordVertex5 = textContentVertexFactory.create(
                traversalSource,
                new TextContentVertexFactory.Options("5", "Random option 3", langVertex));

        var wordVertex6 = textContentVertexFactory.create(
                traversalSource,
                new TextContentVertexFactory.Options("6", "Correct option", langVertex));

        var flashcardVertex1 = flashcardVertexFactory.create(
                traversalSource,
                new FlashcardVertexFactory.Options("flashcardVertex6", wordVertex6, wordVertex6));

        var flashcardVertex2 = flashcardVertexFactory.create(
                traversalSource,
                new FlashcardVertexFactory.Options("flashcardVertex7", wordVertex3, wordVertex3));

        exerciseSelectFlashcardVertexFactory.create(
                traversalSource,
                new ExerciseSelectFlashcardVertexFactory.Options(
                        "3",
                        null,
                        flashcardVertex1,
                        List.of(flashcardVertex2),
                        "front"));

        // Exercise session 4
        var flashcardVertex = FlashcardVertex.findById(traversalSource, "flashcardVertex1");
        exerciseReviewFlashcardVertexFactory.create(
                traversalSource,
                new ExerciseReviewFlashcardVertexFactory.Options("4", null, flashcardVertex));

        // Exercise session 4 - Flashcard exercise session
        var sessionModel = new ExerciseSessionModel();
        sessionModel.setId("4");
        sessionModel.setType("flashcard-review-session");
        sessionModel.setFlashcardDeckId("flashcardDeckVertex1");

        exerciseSessionFlashcardReviewVertexFactory.init(
                traversalSource,
                sessionModel);

        // Exercise session 5 - Flashcard pronounce exercise
        var flashcardVertex3 = flashcardVertexFactory.create(
                traversalSource,
                new FlashcardVertexFactory.Options("flashcardVertex3", wordVertex1, wordVertex1));

        exercisePronounceFlashcardVertexFactory.create(
                traversalSource,
                new ExercisePronounceFlashcardVertexFactory.Options("5", null, flashcardVertex3, "front"));
    }
}
