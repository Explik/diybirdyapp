package com.explik.diybirdyapp.serializer;

import com.explik.diybirdyapp.annotations.Discriminator;
import com.explik.diybirdyapp.annotations.DtoType;
import com.explik.diybirdyapp.model.ExerciseAnswer;
import org.springframework.stereotype.Component;

@Component
public class ExerciseAnswerSerializer extends GenericSerializer<ExerciseAnswer> {
    public ExerciseAnswerSerializer() {
        super("answerType", ExerciseAnswer.class);
    }
}