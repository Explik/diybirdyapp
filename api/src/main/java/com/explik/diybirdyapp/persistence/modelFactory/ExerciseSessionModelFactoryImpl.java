package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsModel;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionProgressModel;
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
    public ExerciseSessionModel create(ExerciseSessionVertex vertex) {
        boolean isCompleted = vertex.getCompleted();

        // Fetch session data
        ExerciseSessionModel model = new ExerciseSessionModel();
        model.setId(vertex.getId());
        model.setType(vertex.getType());
        model.setCompleted(isCompleted);

        if (!isCompleted) {
            var exerciseModel = createExercise(vertex);
            model.setExercise(exerciseModel);
        }

        var progressModel = createProgress(vertex);
        model.setProgress(progressModel);

        var optionsModel = createOptions(vertex);
        model.setOptions(optionsModel);

        return model;
    }

    private ExerciseModel createExercise(ExerciseSessionVertex vertex) {
        var exerciseVertex = vertex.getCurrentExercise();
        if (exerciseVertex == null)
            return null;

        var exerciseType = exerciseVertex.getType();
        var exerciseSchema = ExerciseSchemas.getByType(exerciseType);
        var exerciseFactory = abstractModelFactory.create(exerciseSchema);
        var retrievalContext = new ExerciseRetrievalContextProvider().get(vertex);

        return exerciseFactory.create(exerciseVertex, retrievalContext);
    }

    private ExerciseSessionProgressModel createProgress(ExerciseSessionVertex vertex) {
        ExerciseSessionProgressModel progressModel = new ExerciseSessionProgressModel();
        progressModel.setType("percentage");
        progressModel.setPercentage(90);
        return progressModel;
    }

    private ExerciseSessionOptionsModel createOptions(ExerciseSessionVertex vertex) {
        var optionsVertex = vertex.getOptions();
        if (optionsVertex == null)
            return null;

        var model = new ExerciseSessionOptionsModel();
        model.setTextToSpeechEnabled(optionsVertex.getTextToSpeechEnabled());
        model.setInitialFlashcardLanguageId(optionsVertex.getInitialFlashcardLanguageId());
        model.setRetypeCorrectAnswerEnabled(optionsVertex.getRetypeCorrectAnswer());
        return model;
    }
}
