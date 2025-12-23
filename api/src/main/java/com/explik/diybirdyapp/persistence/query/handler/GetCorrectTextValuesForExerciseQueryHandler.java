package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.persistence.query.GetCorrectTextValuesForExerciseQuery;
import com.explik.diybirdyapp.persistence.query.modelFactory.CorrectTextValuesForExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetCorrectTextValuesForExerciseQueryHandler implements QueryHandler<GetCorrectTextValuesForExerciseQuery, CorrectTextValuesForExerciseModel> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public CorrectTextValuesForExerciseModel handle(GetCorrectTextValuesForExerciseQuery query) {
        // Get exercise vertex
        var exerciseVertex = ExerciseVertex.getById(traversalSource, query.getExerciseId());
        if (exerciseVertex == null)
            throw new IllegalArgumentException("Exercise with id " + query.getExerciseId() + " does not exist");

        // Get correct text options and extract values
        var correctOptions = exerciseVertex.getCorrectOptions();
        if (correctOptions.isEmpty())
            throw new IllegalArgumentException("Exercise has no correct options");

        var correctTextValues = correctOptions.stream()
                .map(option -> {
                    if (option instanceof TextContentVertex textOption) {
                        return textOption.getValue();
                    }
                    throw new IllegalArgumentException("Correct option is not a text content vertex");
                })
                .toList();

        // Create and return model
        var model = new CorrectTextValuesForExerciseModel();
        model.setExerciseId(exerciseVertex.getId());
        model.setExerciseType(exerciseVertex.getExerciseType().getId());
        model.setCorrectTextValues(correctTextValues);

        return model;
    }
}
