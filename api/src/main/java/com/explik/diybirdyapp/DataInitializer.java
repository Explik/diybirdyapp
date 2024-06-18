package com.explik.diybirdyapp;

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
}