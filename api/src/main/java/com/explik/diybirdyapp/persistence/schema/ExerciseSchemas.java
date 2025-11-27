package com.explik.diybirdyapp.persistence.schema;

import com.explik.diybirdyapp.ContentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.ExerciseTypes;

import java.util.Map;

public class ExerciseSchemas {
    public static final ExerciseSchema ARRANGE_WORDS_IN_TRANSLATION = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.ARRANGE_WORDS_IN_TRANSLATION)
            .withContentType(ContentTypes.TEXT)
            .withInputType(ExerciseInputTypes.ARRANGE_TEXT_OPTIONS)
            .withEvaluationType(ExerciseEvaluationTypes.CORRECT_ORDER_OF_OPTIONS);

    public static final ExerciseSchema TAP_PAIRS_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.TAP_PAIRS)
            .withInputType(ExerciseInputTypes.PAIR_OPTIONS)
            .withEvaluationType(ExerciseEvaluationTypes.CORRECT_PAIRS);

    public static final ExerciseSchema SELECT_FLASHCARD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.SELECT_FLASHCARD)
            .withContentType(ContentTypes.FLASHCARD_SIDE)
            .withInputType(ExerciseInputTypes.SELECT_OPTIONS)
            .withEvaluationType(ExerciseEvaluationTypes.CORRECT_OPTIONS);

    public static final ExerciseSchema LISTEN_AND_SELECT_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.LISTEN_AND_SELECT)
            .withContentType(ContentTypes.AUDIO)
            .withInputType(ExerciseInputTypes.SELECT_OPTIONS)
            .withEvaluationType(ExerciseEvaluationTypes.CORRECT_OPTIONS);

    public static final ExerciseSchema LISTEN_AND_WRITE_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.LISTEN_AND_WRITE)
            .withContentType(ContentTypes.AUDIO)
            .withInputType(ExerciseInputTypes.WRITE_TEXT)
            .withEvaluationType(ExerciseEvaluationTypes.CORRECT_TEXT);

    public static final ExerciseSchema PRONOUNCE_FLASHCARD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.PRONOUNCE_FLASHCARD)
            .withContentType(ContentTypes.FLASHCARD_SIDE)
            .withEvaluationType(ExerciseEvaluationTypes.CORRECT_SPEECH_TO_TEXT);

    public static final ExerciseSchema REVIEW_FLASHCARD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.REVIEW_FLASHCARD)
            .withContentType(ContentTypes.FLASHCARD)
            .withEvaluationType(ExerciseEvaluationTypes.RECOGNIZABILITY);

    public static final ExerciseSchema WRITE_FLASHCARD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.WRITE_FLASHCARD)
            .withContentType(ContentTypes.FLASHCARD_SIDE)
            .withInputType(ExerciseInputTypes.WRITE_TEXT)
            .withEvaluationType(ExerciseEvaluationTypes.CORRECT_TEXT);

    public static final ExerciseSchema WRITE_SENTENCE_USING_WORD_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.WRITE_SENTENCE_USING_WORD)
            .withContentType(ContentTypes.TEXT)
            .withEvaluationType(ExerciseEvaluationTypes.CORRECT_TEXT)
            .withModelProperties(Map.of("word", (v) -> v.getTextContent().getValue()))
            .requireTargetLanguage();

    public static final ExerciseSchema WRITE_TRANSLATED_SENTENCE_EXERCISE = new ExerciseSchema()
            .withExerciseType(ExerciseTypes.WRITE_TRANSLATED_SENTENCE)
            .withContentType(ContentTypes.TEXT)
            .withEvaluationType(ExerciseEvaluationTypes.CORRECT_TEXT)
            .withModelProperties(Map.of("targetLanguage", (v) -> v.getTargetLanguage()))
            .requireTargetLanguage();

    public static ExerciseSchema getByType(String type) {
        return switch (type) {
            case ExerciseTypes.ARRANGE_WORDS_IN_TRANSLATION -> ARRANGE_WORDS_IN_TRANSLATION;
            case ExerciseTypes.SELECT_FLASHCARD -> SELECT_FLASHCARD_EXERCISE;
            case ExerciseTypes.TAP_PAIRS ->  TAP_PAIRS_EXERCISE;
            case ExerciseTypes.PRONOUNCE_FLASHCARD -> PRONOUNCE_FLASHCARD_EXERCISE;
            case ExerciseTypes.REVIEW_FLASHCARD -> REVIEW_FLASHCARD_EXERCISE;
            case ExerciseTypes.WRITE_FLASHCARD -> WRITE_FLASHCARD_EXERCISE;
            case ExerciseTypes.WRITE_SENTENCE_USING_WORD -> WRITE_SENTENCE_USING_WORD_EXERCISE;
            case ExerciseTypes.WRITE_TRANSLATED_SENTENCE -> WRITE_TRANSLATED_SENTENCE_EXERCISE;
            case ExerciseTypes.LISTEN_AND_WRITE -> LISTEN_AND_WRITE_EXERCISE;
            case ExerciseTypes.LISTEN_AND_SELECT -> LISTEN_AND_SELECT_EXERCISE;
            default -> throw new RuntimeException("Unknown exercise type: " + type);
        };
    }
}
