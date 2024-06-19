package com.explik.diybirdyapp.serializer;

import com.explik.diybirdyapp.annotations.ExerciseType;
import com.explik.diybirdyapp.model.Exercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSerializer extends GenericSerializer<Exercise, ExerciseType> {
    public ExerciseSerializer() {
        super("exerciseType", Exercise.class, ExerciseType.class, ExerciseType::value);
    }
}

