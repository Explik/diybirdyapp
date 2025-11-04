package com.explik.diybirdyapp.model.exercise;

public class ExerciseSessionOptionsReviewFlashcardsModel extends ExerciseSessionOptionsModel {
    private String initialFlashcardLanguageId;
    private String[] availableFlashcardLanguageIds;

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
