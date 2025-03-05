package com.explik.diybirdyapp.persistence.provider;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.persistence.strategy.ExerciseEvaluationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ExerciseEvaluationStrategyProvider implements GenericProvider<ExerciseEvaluationStrategy> {
    @Autowired
    private Map<String, ExerciseEvaluationStrategy> exerciseManagers;

    @Override
    public ExerciseEvaluationStrategy get(String exerciseType) {
        var operations = exerciseManagers.getOrDefault(exerciseType + ComponentTypes.OPERATIONS, null);
        if (operations == null)
            throw new RuntimeException("Unsupported exercise type: " + exerciseType);

        return operations;
    }
}
