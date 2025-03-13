package com.explik.diybirdyapp.persistence.provider;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExerciseSessionOperationProvider implements GenericProvider<ExerciseSessionOperations> {
    @Autowired
    Map<String, ExerciseSessionOperations> sessionManagers;

    @Override
    public ExerciseSessionOperations get(String type) {
        var sessionManager = sessionManagers.getOrDefault(type + ComponentTypes.OPERATIONS, null);
        if (sessionManager == null)
            throw new IllegalArgumentException("No factory for type " + type);
        return sessionManager;
    }
}
