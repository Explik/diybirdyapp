package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.dto.exercise.ExerciseDto;
import com.explik.diybirdyapp.dto.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.dto.exercise.ExerciseSessionProgressDto;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContextProvider;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionModelFactoryImpl implements ExerciseSessionModelFactory {
    @Autowired
    ExerciseAbstractModelFactory abstractModelFactory;

    @Override
    public ExerciseSessionDto create(ExerciseSessionVertex vertex) {
        boolean isCompleted = vertex.getCompleted();

        // Fetch session data
        ExerciseSessionDto model = new ExerciseSessionDto();
        model.setId(vertex.getId());
        model.setType(vertex.getType());
        model.setCompleted(isCompleted);

        if (!isCompleted) {
            var exerciseModel = createExercise(vertex);
            model.setExercise(exerciseModel);
        }

        var progressModel = createProgress(vertex);
        model.setProgress(progressModel);

        return model;
    }

    private ExerciseDto createExercise(ExerciseSessionVertex vertex) {
        var exerciseVertex = vertex.getCurrentExercise();
        if (exerciseVertex == null)
            return null;

        var exerciseType = exerciseVertex.getType();
        var exerciseSchema = ExerciseSchemas.getByType(exerciseType);
        var exerciseFactory = abstractModelFactory.create(exerciseSchema);
        var retrievalContext = new ExerciseRetrievalContextProvider().get(vertex);

        return exerciseFactory.create(exerciseVertex, retrievalContext);
    }

    private ExerciseSessionProgressDto createProgress(ExerciseSessionVertex vertex) {
        ExerciseSessionProgressDto progressModel = new ExerciseSessionProgressDto();
        progressModel.setType("percentage");
        progressModel.setPercentage(90);
        return progressModel;
    }
}
