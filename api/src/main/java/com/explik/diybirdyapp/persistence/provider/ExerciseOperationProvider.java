package com.explik.diybirdyapp.persistence.provider;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.persistence.operation.ExerciseOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ExerciseOperationProvider implements GenericProvider<ExerciseOperations> {
    @Autowired
    private Map<String, ExerciseOperations> exerciseManagers;

    @Override
    public ExerciseOperations get(String exerciseType) {
        var operations = exerciseManagers.getOrDefault(exerciseType + ComponentTypes.OPERATIONS, null);
        if (operations == null)
            throw new RuntimeException("Unsupported exercise type: " + exerciseType);

        return operations;
    }
}
