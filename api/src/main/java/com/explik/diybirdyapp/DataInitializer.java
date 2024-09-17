package com.explik.diybirdyapp;

import com.explik.diybirdyapp.graph.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private GraphTraversalSource traversalSource;

    @Override
    public void run(String... args) throws Exception {
        addInitialExerciseData();
        addInitialFlashcardAndFlashcardExerciseData();
    }

    public void addInitialExerciseData() {
        var exerciseVertex1 = ExerciseVertex.create(traversalSource);
        exerciseVertex1.setId("1");
        exerciseVertex1.setType("write-sentence-using-word-exercise");

        var wordVertex1 = TextContentVertex.create(traversalSource);
        wordVertex1.setId("1");
        wordVertex1.setValue("example");

        exerciseVertex1.setContent(wordVertex1);

        // Exercise 2 - Translate sentence to Danish
        var exerciseVertex2 = ExerciseVertex.create(traversalSource);
        exerciseVertex2.setId("2");
        exerciseVertex2.setType("write-translated-sentence-exercise");
        exerciseVertex2.setTargetLanguage("Danish");

        var wordVertex2 = TextContentVertex.create(traversalSource);
        wordVertex2.setId("2");
        wordVertex2.setValue("This is an example sentence");

        exerciseVertex2.setContent(wordVertex2);

        // Exercise 3 - Multiple choice text exercise
        var exerciseVertex3 = ExerciseVertex.create(traversalSource);
        exerciseVertex3.setId("3");
        exerciseVertex3.setType("multiple-choice-text-exercise");

        var wordVertex3 = TextContentVertex.create(traversalSource);
        wordVertex3.setId("3");
        wordVertex3.setValue("Random option 1");

        var wordVertex4 = TextContentVertex.create(traversalSource);
        wordVertex4.setId("4");
        wordVertex4.setValue("Random option 2");

        var wordVertex5 = TextContentVertex.create(traversalSource);
        wordVertex5.setId("5");
        wordVertex5.setValue("Random option 3");

        var wordVertex6 = TextContentVertex.create(traversalSource);
        wordVertex6.setId("6");
        wordVertex6.setValue("Correct option");

        exerciseVertex3.addOption(wordVertex3);
        exerciseVertex3.addOption(wordVertex4);
        exerciseVertex3.addOption(wordVertex5);
        exerciseVertex3.addOption(wordVertex6);
        exerciseVertex3.setCorrectOption(wordVertex6);
    }

    public void addInitialFlashcardAndFlashcardExerciseData() {
        var langVertex1 = LanguageVertex.create(traversalSource);
        langVertex1.setId("langVertex1");
        langVertex1.setAbbreviation("DA");
        langVertex1.setName("Danish");

        var langVertex2 = LanguageVertex.create(traversalSource);
        langVertex2.setId("langVertex2");
        langVertex2.setAbbreviation("EN");
        langVertex2.setName("English");

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

        // Exercise 4 - Flashcard exercise
        var exerciseVertex1 = ExerciseVertex.create(traversalSource);
        exerciseVertex1.setId("4");
        exerciseVertex1.setType("review-flashcard-content-exercise");
        exerciseVertex1.setContent(flashcardVertex1);
    }
}