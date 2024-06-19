package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.converter.ExerciseConverter;
import com.explik.diybirdyapp.model.Exercise;
import com.explik.diybirdyapp.model.GenericExercise;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ExerciseService {
    private final Map<String, ExerciseConverter<? extends Exercise>> converters = new HashMap<>();

    @Autowired
    private TinkerGraph graph;

    @Autowired
    public ExerciseService(List<ExerciseConverter<? extends Exercise>> converters) {
        converters.forEach(c -> this.converters.put(c.getExerciseType(), c));
    }

    public Exercise createExercise(Exercise<?> data) {
        var converter = GetConverter(data);
        converter.create(graph.traversal(), data);

        return getExercise(data.getId());
    }

    public Exercise createExerciseAnswer(Exercise<?> data) {
        if (data.getId() == null)
            throw new IllegalArgumentException("Missing exercise id");
        if (data.getExerciseAnswer() == null)
            throw new IllegalArgumentException("Missing exercise answer");

        var converter = GetConverter(data);
        converter.update(graph.traversal(), data);

        return getExercise(data.getId());
    }

    public Exercise getExercise(String id) {
        Vertex exerciseVertex = graph.traversal().V().hasLabel("exercise")
                .has("id", id)
                .next();
        if (exerciseVertex == null)
            throw new IllegalArgumentException("Exercise " + id + " was not found");

        String exerciseType = exerciseVertex.value("exerciseType");
        ExerciseConverter<? extends Exercise> exerciseConverter = converters.get(exerciseType);
        if (exerciseConverter == null) {
            throw new IllegalArgumentException("Exercise type " + exerciseType + " is not supported");
        }

        return exerciseConverter.get(graph.traversal(), exerciseVertex);
    }

    public List<Exercise> getExercises() {
        return graph.traversal().V().hasLabel("exercise")
                .toStream()
                .map(v -> new GenericExercise(v.value("id"), v.value("exerciseType")))
                .collect(Collectors.toList());
    }

    private ExerciseConverter<?> GetConverter(Exercise<?> exercise) {
        String exerciseType = exercise.getExerciseType();
        ExerciseConverter<? extends Exercise> converter = converters.get(exerciseType);

        if (converter == null)
            throw new IllegalArgumentException("Unknown exercise type: " + exerciseType);

        return converter;
    }
}
