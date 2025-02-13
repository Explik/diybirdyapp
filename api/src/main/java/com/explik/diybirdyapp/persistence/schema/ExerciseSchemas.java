package com.explik.diybirdyapp.persistence.schema;

import com.explik.diybirdyapp.ContentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.ExerciseTypes;

public class ExerciseSchemas {
    public static final ExerciseSchema SELECT_FLASHCARD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.SELECT_FLASHCARD)
            .withContentType(ContentTypes.FLASHCARD)
            .withInputType(ExerciseInputTypes.SELECT_OPTIONS);

    public static final ExerciseSchema PRONOUNCE_FLASHCARD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.PRONOUNCE_FLASHCARD)
            .withContentType(ContentTypes.FLASHCARD_SIDE);

    public static final ExerciseSchema REVIEW_FLASHCARD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.REVIEW_FLASHCARD)
            .withContentType(ContentTypes.FLASHCARD);

    public static final ExerciseSchema WRITE_FLASHCARD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.WRITE_FLASHCARD)
            .withContentType(ContentTypes.FLASHCARD_SIDE)
            .withInputType(ExerciseInputTypes.WRITE_TEXT);

    public static final ExerciseSchema WRITE_SENTENCE_USING_WORD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.WRITE_SENTENCE_USING_WORD)
            .withContentType(ContentTypes.TEXT)
            .withInputType(ExerciseInputTypes.WRITE_TEXT)
            .requireTargetLanguage();

    public static final ExerciseSchema WRITE_TRANSLATED_SENTENCE_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.WRITE_TRANSLATED_SENTENCE)
            .withContentType(ContentTypes.TEXT)
            .withInputType(ExerciseInputTypes.WRITE_TEXT)
            .requireTargetLanguage();
}
