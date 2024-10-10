package com.explik.diybirdyapp.persistence.repository;

import java.util.List;

import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.ExerciseModel;

public interface ExerciseRepository {
    ExerciseModel get(String id);

    List<ExerciseModel> getAll();

    ExerciseFeedbackModel submitAnswer(String id, ExerciseAnswerModel answer);
}
