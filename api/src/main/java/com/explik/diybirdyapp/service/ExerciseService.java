package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.Exercise;
import jakarta.annotation.PostConstruct;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExerciseService {
    @Autowired
    private TinkerGraph graph;

    public Exercise getExercise(long id) {
        Vertex actionVertex = graph.traversal().V().hasLabel("action")
                .has("id", id + "")
                .has("exerciseType", "write-sentence-exercise")
                .next();

        if (actionVertex == null) {
            return null; // Or throw an exception if preferred
        }

        Vertex wordVertex = graph.traversal().V(actionVertex.id())
                .out("relatedTo")
                .hasLabel("word")
                .next();

        if (wordVertex == null) {
            return null; // Or throw an exception if preferred
        }

        Exercise exercise = new Exercise();
        exercise.setId(actionVertex.property("id").value().toString());
        exercise.setExerciseType("write-sentence-exercise");
        exercise.setWord(wordVertex.property("word").value().toString());

        return exercise;
    }
}
