package com.explik.diybirdyapp.serializer;

import com.explik.diybirdyapp.annotations.ExerciseAnswerType;
import com.explik.diybirdyapp.model.ExerciseAnswer;
import org.springframework.stereotype.Component;

@Component
public class ExerciseAnswerSerializer extends GenericSerializer<ExerciseAnswer, ExerciseAnswerType> {
    public ExerciseAnswerSerializer() {
        super("exerciseType", ExerciseAnswer.class, ExerciseAnswerType.class, ExerciseAnswerType::value);
    }
}