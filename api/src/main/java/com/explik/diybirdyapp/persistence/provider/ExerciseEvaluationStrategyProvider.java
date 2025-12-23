package com.explik.diybirdyapp.persistence.provider;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.manager.exerciseEvaluationManager.ExerciseEvaluationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ExerciseEvaluationStrategyProvider implements GenericProvider<ExerciseEvaluationManager> {
    @Autowired
    private Map<String, ExerciseEvaluationManager> exerciseManagers;

    @Override
    public ExerciseEvaluationManager get(String exerciseType) {
        if (exerciseType == null)
            throw new RuntimeException("Exercise type is required");

        var exerciseSchema = ExerciseSchemas.getByType(exerciseType);
        if (exerciseSchema == null)
            throw new RuntimeException("Unsupported exercise type: " + exerciseType);

        var evaluationType = exerciseSchema.getEvaluationType();
        var evaluationStrategy = exerciseManagers.getOrDefault(evaluationType + ComponentTypes.STRATEGY, null);
        if (evaluationStrategy == null)
            throw new RuntimeException("Unsupported evaluation type: " + evaluationType);

        return evaluationStrategy;
    }
}
