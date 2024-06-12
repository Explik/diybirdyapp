package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.Exercise;
import jakarta.annotation.PostConstruct;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.stereotype.Service;

@Service
public class ExerciseService {
    private TinkerGraph graph;

    @PostConstruct
    public void init() {
        graph = TinkerGraph.open();

        graph.addVertex(T.label, "exercise", "id", 30);
        graph.addVertex(T.label, "exercise", "id", 40);
        graph.addVertex(T.label, "exercise", "id", 50);
    }

    public Exercise getExercise(long id) {
        Vertex v = graph.traversal().V().has("id", id).tryNext().orElse(null);
        return new Exercise(id + 1);
    }
}
