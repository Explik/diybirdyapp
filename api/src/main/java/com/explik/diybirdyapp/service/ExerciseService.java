package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.ExerciseInputModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.persistence.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {
    @Autowired
    ExerciseRepository repository;

    public ExerciseModel getExercise(String id) {
        return repository.get(id);
    }

    public ExerciseModel submitExerciseAnswer(String id, ExerciseInputModel answer) {
        return repository.submitAnswer(id, answer);
    }

    public List<ExerciseModel> getExercises() {
        return repository.getAll();
    }
}
