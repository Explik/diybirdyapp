package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
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

    public void resetInitialData() {
        traversalSource.V().drop().iterate();
        appendInitialData();
    }

    public void appendInitialData() {
        addInitialLanguageData();
        addInitialFlashcardData();
        addInitialExerciseData();
    }

    public void addInitialLanguageData() {
        languageVertexFactory.create(
                traversalSource,
                new LanguageVertexFactory.Options("langVertex1", "Danish", "DA"));

        languageVertexFactory.create(
                traversalSource,
                new LanguageVertexFactory.Options("langVertex2", "English", "EN"));
    }

    public void addInitialFlashcardData() {
        var langVertex1 = LanguageVertex.findByAbbreviation(traversalSource, "DA");
        var langVertex2 = LanguageVertex.findByAbbreviation(traversalSource, "EN");

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

        exerciseSelectFlashcardVertexFactory.create(
                traversalSource,
                new ExerciseSelectFlashcardVertexFactory.Options(
                        "3",
                        null,
                        wordVertex6,
                        List.of(wordVertex3, wordVertex4, wordVertex5),
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
    }
}
