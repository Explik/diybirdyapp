package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.model.ExerciseSessionProgressModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExerciseSessionModelFactoryImpl implements ExerciseSessionModelFactory {
    @Autowired
    Map<String, ExerciseModelFactory> exerciseModelFactories;

    @Override
    public ExerciseSessionModel create(ExerciseSessionVertex vertex) {
        // Fetch session data
        ExerciseSessionModel model = new ExerciseSessionModel();
        model.setId(vertex.getId());
        model.setType(vertex.getType());

        ExerciseModel exerciseModel = createExercise(vertex);
        model.setExercise(exerciseModel);

        ExerciseSessionProgressModel progressModel = createProgress(vertex);
        model.setProgress(progressModel);

        return model;
    }

    private ExerciseModel createExercise(ExerciseSessionVertex vertex) {
        var exerciseVertex = vertex.getCurrentExercise();
        if (exerciseVertex == null)
            return null;

        var exerciseType = exerciseVertex.getType();
        var exerciseModelFactory = exerciseModelFactories.getOrDefault(exerciseType + ComponentTypes.MODEL_FACTORY, null);
        if (exerciseModelFactory == null)
            throw new RuntimeException("Unsupported exercise type " + exerciseType);

        return exerciseModelFactory.create(exerciseVertex);
    }

    private ExerciseSessionProgressModel createProgress(ExerciseSessionVertex vertex) {
        ExerciseSessionProgressModel progressModel = new ExerciseSessionProgressModel();
        progressModel.setType("percentage");
        progressModel.setPercentage(90);
        return progressModel;
    }
}
