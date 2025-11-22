package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.dto.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.dto.exercise.ExerciseSessionOptionsDto;

public interface ExerciseSessionRepository {
    ExerciseSessionDto add(ExerciseSessionDto model);
    ExerciseSessionDto get(String sessionId);
    ExerciseSessionDto nextExercise(String sessionId);

    ExerciseSessionOptionsDto getConfig(String sessionId);
    ExerciseSessionDto updateConfig(String modelId, ExerciseSessionOptionsDto config);
}
