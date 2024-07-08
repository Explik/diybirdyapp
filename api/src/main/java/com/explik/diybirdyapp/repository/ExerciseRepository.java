package com.explik.diybirdyapp.repository;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import com.explik.diybirdyapp.model.Exercise;

public interface ExerciseRepository {
    void add(Exercise exercise);

    Exercise getById(String id) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    List<Exercise> getAll();
}
