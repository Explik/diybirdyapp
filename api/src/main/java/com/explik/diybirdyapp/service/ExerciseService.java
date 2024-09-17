package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.repository.ExerciseRepository;
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

    public List<ExerciseModel> getExercises() {
        return repository.getAll();
    }
}
