package com.explik.diybirdyapp.service;
import com.explik.diybirdyapp.ExerciseTypes;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ExerciseTypeService {
    public List<String> getAll() {
        return Arrays.asList(ExerciseTypes.getAll());
    }
}
