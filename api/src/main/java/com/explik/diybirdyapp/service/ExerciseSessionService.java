package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsModel;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.repository.ExerciseSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionService {
    @Autowired
    ExerciseSessionRepository sessionRepository;

    public ExerciseSessionModel add(ExerciseSessionModel model) {
        return sessionRepository.add(model);
    }

    public ExerciseSessionModel get(String id) {
        return sessionRepository.get(id);
    }

    public ExerciseSessionModel nextExercise(String modelId) {
        return sessionRepository.nextExercise(modelId);
    }

    public ExerciseSessionModel skipExercise(String modelId) {
        return sessionRepository.nextExercise(modelId);
    }

    public ExerciseSessionOptionsModel getConfig(String modelId) {
        return sessionRepository.getConfig(modelId);
    }

    public ExerciseSessionModel updateConfig(String modelId, ExerciseSessionOptionsModel config) {
        return sessionRepository.updateConfig(modelId, config);
    }
}
