package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.Exercise;
import com.explik.diybirdyapp.model.WriteSentenceUsingWordExercise;
import com.explik.diybirdyapp.converter.ExerciseConverter;
import jakarta.annotation.PostConstruct;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExerciseService {
    private final Map<String, ExerciseConverter<? extends Exercise>> converters = new HashMap<>();

    @Autowired
    private TinkerGraph graph;

    @Autowired
    public ExerciseService(List<ExerciseConverter<? extends Exercise>> converters) {
        converters.forEach(c -> this.converters.put(c.getExerciseType(), c));
    }

    public Exercise getExercise(long id) {
        Vertex exerciseVertex = graph.traversal().V().hasLabel("exercise")
                .has("id", id + "")
                .next();
        if (exerciseVertex == null)
            throw new IllegalArgumentException("Exercise " + id + " was not found");

        String exerciseType = exerciseVertex.value("exerciseType");
        ExerciseConverter<? extends Exercise> exerciseConverter = converters.get(exerciseType);
        if (exerciseConverter == null) {
            throw new IllegalArgumentException("Exercise type " + exerciseType + " is not supported");
        }

        return exerciseConverter.convert(graph.traversal(), exerciseVertex);
    }
}
