package com.explik.diybirdyapp.graph.repository;

import java.util.List;

import com.explik.diybirdyapp.graph.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.graph.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.graph.model.ExerciseModel;

public interface ExerciseRepository {
    ExerciseModel get(String id);

    List<ExerciseModel> getAll();

    ExerciseFeedbackModel submitAnswer(String id, ExerciseAnswerModel answer);
}
