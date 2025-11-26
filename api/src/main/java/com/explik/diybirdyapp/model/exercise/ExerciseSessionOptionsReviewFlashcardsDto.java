package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsReviewFlashcardsDto extends ExerciseSessionOptionsDto {
    private String initialFlashcardLanguageId;
    private ExerciseSessionOptionsLanguageOptionDto[] availableFlashcardLanguages = new ExerciseSessionOptionsLanguageOptionDto[0];

    public ExerciseSessionOptionsReviewFlashcardsDto() {
        super(ExerciseSessionTypes.REVIEW_FLASHCARD);
    }

    public String getInitialFlashcardLanguageId() {
        return initialFlashcardLanguageId;
    }

    public void setInitialFlashcardLanguageId(String initialFlashcardLanguageId) {
        this.initialFlashcardLanguageId = initialFlashcardLanguageId;
    }

    public ExerciseSessionOptionsLanguageOptionDto[] getAvailableFlashcardLanguages() {
        return availableFlashcardLanguages;
    }

    public void setAvailableFlashcardLanguages(ExerciseSessionOptionsLanguageOptionDto[] availableFlashcardLanguageIds) {
        this.availableFlashcardLanguages = availableFlashcardLanguageIds;
    }
}
