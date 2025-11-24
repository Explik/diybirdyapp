package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsReviewFlashcardsDto extends ExerciseSessionOptionsDto {
    private String initialFlashcardLanguageId;
    private String[] availableFlashcardLanguageIds = new String[0];

    public ExerciseSessionOptionsReviewFlashcardsDto() {
        super(ExerciseSessionTypes.REVIEW_FLASHCARD);
    }

    public String getInitialFlashcardLanguageId() {
        return initialFlashcardLanguageId;
    }

    public void setInitialFlashcardLanguageId(String initialFlashcardLanguageId) {
        this.initialFlashcardLanguageId = initialFlashcardLanguageId;
    }

    public String[] getAvailableFlashcardLanguageIds() {
        return availableFlashcardLanguageIds;
    }

    public void setAvailableFlashcardLanguageIds(String[] availableFlashcardLanguageIds) {
        this.availableFlashcardLanguageIds = availableFlashcardLanguageIds;
    }
}
