package com.explik.diybirdyapp.dto.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsSelectFlashcardsDto extends ExerciseSessionOptionsDto {
    private String initialFlashcardLanguageId;
    private String[] availableFlashcardLanguageIds = new String[0];

    public ExerciseSessionOptionsSelectFlashcardsDto() {
        super(ExerciseSessionTypes.SELECT_FLASHCARD_DECK);
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
