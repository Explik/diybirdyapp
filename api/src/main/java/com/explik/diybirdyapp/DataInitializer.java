package com.explik.diybirdyapp;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private Graph graph;

    @Override
    public void run(String... args) throws Exception {
        addInitialExerciseData();
        addInitialFlashcardData();
    }

    public void addInitialExerciseData() {
        // Exercise 1 - Write sentence using word "example"
        Vertex exerciseVertex1 = graph.addVertex("exercise");
        exerciseVertex1.property("id", "1");
        exerciseVertex1.property("exerciseType", "write-sentence-using-word-exercise");

        Vertex wordVertex1 = graph.addVertex("text");
        wordVertex1.property("value", "example");

        exerciseVertex1.addEdge("basedOn", wordVertex1);

        // Exercise 2 - Translate sentence to Danish
        Vertex exerciseVertex2 = graph.addVertex("exercise");
        exerciseVertex2.property("id", "2");
        exerciseVertex2.property("exerciseType", "write-translated-sentence-exercise");
        exerciseVertex2.property("targetLanguage", "Danish");

        Vertex wordVertex2 = graph.addVertex("text");
        wordVertex2.property("value", "This is an example sentence");

        exerciseVertex2.addEdge("basedOn", wordVertex2);
    }

    public void addInitialFlashcardData() {
        Vertex langVertex1 = graph.addVertex("language");
        langVertex1.property("abbreviation", "DA");
        langVertex1.property("name", "Danish");

        Vertex langVertex2 = graph.addVertex("language");
        langVertex2.property("abbreviation", "EN");
        langVertex2.property("name", "English");

        Vertex textVertex1 = graph.addVertex("textContent");
        textVertex1.property("value", "Hej verden");
        textVertex1.addEdge("hasLanguage", langVertex1);

        Vertex textVertex2 = graph.addVertex("textContent");
        textVertex2.property("value", "Hello world");
        textVertex2.addEdge("hasLanguage", langVertex2);

        Vertex textVertex3 = graph.addVertex("textContent");
        textVertex3.property("value", "Hej John");
        textVertex3.addEdge("hasLanguage", langVertex1);

        Vertex textVertex4 = graph.addVertex("textContent");
        textVertex4.property("value", "Hey John");
        textVertex4.addEdge("hasLanguage", langVertex2);

        Vertex flashcardVertex1 = graph.addVertex("flashcard");
        flashcardVertex1.addEdge("hasLeftContent", textVertex1);
        flashcardVertex1.addEdge("hasRightContent", textVertex2);

        Vertex flashcardVertex2 = graph.addVertex("flashcard");
        flashcardVertex2.addEdge("hasLeftContent", textVertex3);
        flashcardVertex2.addEdge("hasRightContent", textVertex4);
    }
}