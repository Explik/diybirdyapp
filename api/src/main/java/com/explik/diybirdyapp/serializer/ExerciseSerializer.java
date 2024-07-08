package com.explik.diybirdyapp.serializer;

import com.explik.diybirdyapp.annotations.Discriminator;
import com.explik.diybirdyapp.model.Exercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSerializer extends GenericSerializer<Exercise> {
    public ExerciseSerializer() {
        super("exerciseType", Exercise.class);
    }
}

