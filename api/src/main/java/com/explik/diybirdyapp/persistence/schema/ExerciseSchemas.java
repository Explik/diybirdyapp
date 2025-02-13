package com.explik.diybirdyapp.persistence.schema;

import com.explik.diybirdyapp.ContentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.ExerciseTypes;

public class ExerciseSchemas {
    public static final ExerciseSchema SELECT_FLASHCARD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.SELECT_FLASHCARD)
            .withContentType(ContentTypes.FLASHCARD)
            .withInputType(ExerciseInputTypes.SELECT_OPTIONS);
}
