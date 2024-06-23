package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.graph.operation.*;
import com.explik.diybirdyapp.graph.model.Exercise;
import com.explik.diybirdyapp.graph.model.ExerciseFeedback;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {
    @Autowired
    private TinkerGraph graph;

    @Autowired
    private AddGenericExerciseCommand addExerciseCommand;

    @Autowired
    private AddGenericExerciseAnswerCommand addExerciseAnswerCommand;

    @Autowired
    private GetGenericExerciseQuery getExerciseQuery;

    @Autowired
    private GetGenericExerciseListQuery getExerciseListQuery;

    @Autowired
    private GetExerciseFeedbackQuery getExerciseFeedbackQuery;

    public Exercise createExercise(Exercise<?> data) {
        addExerciseCommand.execute(
                graph.traversal(),
                new AddGenericExerciseCommand.Options(data));

        return getExercise(data.getId());
    }

    public ExerciseFeedback createExerciseAnswer(Exercise<?> data) {
        if (data.getId() == null)
            throw new IllegalArgumentException("Missing exercise id");
        if (data.getExerciseAnswer() == null)
            throw new IllegalArgumentException("Missing exercise answer");

        addExerciseAnswerCommand.execute(graph.traversal(), new AddGenericExerciseAnswerCommand.Options(
                data.getId(),
                data.getExerciseAnswer()));

        return getExerciseFeedbackQuery.apply(graph.traversal(), new GetExerciseFeedbackQuery.Options(
                data.getId(),
                data.getExerciseAnswer()));
    }

    public Exercise getExercise(String id) {
        return getExerciseQuery.apply(
                graph.traversal(),
                new GetGenericExerciseQuery.Options(id));
    }

    public List<Exercise> getExercises() {
        return getExerciseListQuery.apply(
                graph.traversal(),
                new GetGenericExerciseListQuery.Options());
    }
}
