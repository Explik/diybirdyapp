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
        Vertex actionVertex = graph.addVertex("action");
        actionVertex.property("id", "1");
        actionVertex.property("exerciseType", "write-sentence-exercise");

        Vertex wordVertex = graph.addVertex("word");
        wordVertex.property("word", "example");

        actionVertex.addEdge("relatedTo", wordVertex);
    }
}