package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.persistence.query.GetOptionsForExerciseQuery;
import com.explik.diybirdyapp.persistence.query.modelFactory.OptionsForExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetOptionsForExerciseQueryHandler implements QueryHandler<GetOptionsForExerciseQuery, OptionsForExerciseModel> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public OptionsForExerciseModel handle(GetOptionsForExerciseQuery query) {
        // Get exercise vertex
        var exerciseVertex = ExerciseVertex.getById(traversalSource, query.getExerciseId());
        if (exerciseVertex == null)
            throw new IllegalArgumentException("Exercise with id " + query.getExerciseId() + " does not exist");

        // Get correct and incorrect options
        var correctOptions = exerciseVertex.getCorrectOptions();
        if (correctOptions.isEmpty())
            throw new IllegalArgumentException("Exercise has no correct options");

        var correctOptionVertex = correctOptions.getFirst();
        var incorrectOptionVertices = exerciseVertex.getOptions();

        // Extract IDs
        var correctOptionId = correctOptionVertex.getId();
        var incorrectOptionIds = incorrectOptionVertices.stream()
                .map(ContentVertex::getId)
                .toList();

        // Create combined list of all option IDs
        var allOptionIds = new ArrayList<String>();
        allOptionIds.add(correctOptionId);
        allOptionIds.addAll(incorrectOptionIds);

        // Create and return model
        var model = new OptionsForExerciseModel();
        model.setCorrectOptionId(correctOptionId);
        model.setIncorrectOptionIds(incorrectOptionIds);
        model.setAllOptionIds(allOptionIds);

        return model;
    }
}
