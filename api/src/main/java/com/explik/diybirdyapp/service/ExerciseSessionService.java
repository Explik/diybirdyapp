package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.repository.ExerciseRepository;
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
}
