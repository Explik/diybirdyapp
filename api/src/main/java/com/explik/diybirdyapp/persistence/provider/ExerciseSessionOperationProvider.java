package com.explik.diybirdyapp.persistence.provider;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.manager.exerciseSessionManager.ExerciseSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExerciseSessionOperationProvider implements GenericProvider<ExerciseSessionManager> {
    @Autowired
    Map<String, ExerciseSessionManager> sessionManagers;

    @Override
    public ExerciseSessionManager get(String type) {
        var sessionManager = sessionManagers.getOrDefault(type + ComponentTypes.OPERATIONS, null);
        if (sessionManager == null)
            throw new IllegalArgumentException("No factory for type " + type);
        return sessionManager;
    }
}
