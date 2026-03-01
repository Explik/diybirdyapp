package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionProgressDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;

/**
 * Factory interface for creating progress DTOs for exercise sessions.
 * Different session types may have different progress calculation strategies.
 */
public interface ExerciseSessionProgressFactory {
    /**
     * Creates a progress DTO for the given session.
     * @param vertex The exercise session vertex
     * @return The progress DTO, or null if progress is not applicable
     */
    ExerciseSessionProgressDto createProgress(ExerciseSessionVertex vertex);
}
