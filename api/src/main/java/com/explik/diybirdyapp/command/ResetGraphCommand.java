package com.explik.diybirdyapp.command;

import com.explik.diybirdyapp.graph.vertex.*;
import com.explik.diybirdyapp.graph.vertex.factory.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import java.util.List;

@Component
@CommandLine.Command(name = "reset-graph", description = "Clears graph and inserts dummy data")
public class ResetGraphCommand implements Runnable{
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private ExerciseMultipleChoiceTextVertexFactory exerciseMultipleChoiceTextVertexFactory;

    @Autowired
    private ExerciseWriteSentenceUsingWordVertexFactory exerciseWriteSentenceUsingWordVertexFactory;

    @Autowired
    private ExerciseWriteTranslatedSentenceVertexFactory exerciseWriteTranslatedSentenceVertexFactory;

    @Autowired
    private FlashcardVertexFactory flashcardVertexFactory;

    @Autowired
    private FlashcardDeckVertexFactory flashcardDeckVertexFactory;

    @Autowired
    private LanguageVertexFactory languageVertexFactory;

    @Autowired
    private TextContentVertexFactory textContentVertexFactory;

    @Override
    public void run() {
        traversalSource.V().drop().iterate();

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

        exerciseMultipleChoiceTextVertexFactory.create(
                traversalSource,
                new ExerciseMultipleChoiceTextVertexFactory.Options(
                        "3",
                        List.of(wordVertex3, wordVertex4, wordVertex5, wordVertex6),
                        wordVertex6));

        // Exercise 4 - Flashcard exercise
        var flashcardVertex1 = FlashcardVertex.findById(traversalSource, "flashcardVertex1");

        var exerciseVertex1 = ExerciseVertex.create(traversalSource);
        exerciseVertex1.setId("4");
        exerciseVertex1.setType("review-flashcard-content-exercise");
        exerciseVertex1.setContent(flashcardVertex1);
    }
}
