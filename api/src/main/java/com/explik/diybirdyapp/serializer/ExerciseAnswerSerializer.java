package com.explik.diybirdyapp.serializer;

import com.explik.diybirdyapp.annotations.ExerciseAnswerType;
import com.explik.diybirdyapp.graph.model.ExerciseAnswer;
import org.springframework.stereotype.Component;

@Component
public class ExerciseAnswerSerializer extends GenericSerializer<ExerciseAnswer, ExerciseAnswerType> {
    public ExerciseAnswerSerializer() {
        super("answerType", ExerciseAnswer.class, ExerciseAnswerType.class, ExerciseAnswerType::value);
    }
}