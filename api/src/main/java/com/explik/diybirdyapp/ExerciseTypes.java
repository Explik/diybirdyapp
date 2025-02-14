package com.explik.diybirdyapp;

public class ExerciseTypes {
    public static final String WRITE_SENTENCE_USING_WORD = "write-sentence-using-word-exercise";
    public static final String WRITE_TRANSLATED_SENTENCE = "write-translated-sentence-exercise";
    public static final String MULTIPLE_CHOICE_TEXT = "multiple-choice-text-exercise";

    public static final String ARRANGE_WORDS_IN_TRANSLATION = "arrange-words-in-translation-exercise";
    public static final String WRITE_FLASHCARD = "write-flashcard-exercise";
    public static final String REVIEW_FLASHCARD = "review-flashcard-exercise";
    public static final String SELECT_FLASHCARD = "select-flashcard-exercise";
    public static final String TAP_PAIRS = "tap-pairs-exercise";
    public static final String PRONOUNCE_FLASHCARD = "pronounce-flashcard-exercise";

    public static String[] getAll() {
        return new String[] {
                ARRANGE_WORDS_IN_TRANSLATION,
            WRITE_SENTENCE_USING_WORD,
            WRITE_TRANSLATED_SENTENCE,
            MULTIPLE_CHOICE_TEXT,
            WRITE_FLASHCARD,
            REVIEW_FLASHCARD,
            SELECT_FLASHCARD,
            TAP_PAIRS,
            PRONOUNCE_FLASHCARD
        };
    }
}
