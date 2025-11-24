package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsDto;
import com.explik.diybirdyapp.persistence.repository.ExerciseSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionService {
    @Autowired
    ExerciseSessionRepository sessionRepository;

    public ExerciseSessionDto add(ExerciseSessionDto model) {
        return sessionRepository.add(model);
    }

    public ExerciseSessionDto get(String id) {
        return sessionRepository.get(id);
    }

    public ExerciseSessionDto nextExercise(String modelId) {
        return sessionRepository.nextExercise(modelId);
    }

    public ExerciseSessionDto skipExercise(String modelId) {
        return sessionRepository.nextExercise(modelId);
    }

    public ExerciseSessionOptionsDto getConfig(String modelId) {
        return sessionRepository.getConfig(modelId);
    }

    public ExerciseSessionDto updateConfig(String modelId, ExerciseSessionOptionsDto config) {
        return sessionRepository.updateConfig(modelId, config);
    }
}
