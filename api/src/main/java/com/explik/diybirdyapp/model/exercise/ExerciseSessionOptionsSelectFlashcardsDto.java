package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsSelectFlashcardsDto extends ExerciseSessionOptionsDto {
    private boolean initiallyHideOptions;
    private String initialFlashcardLanguageId;
    private ExerciseSessionOptionsLanguageOptionDto[] availableFlashcardLanguages = new ExerciseSessionOptionsLanguageOptionDto[0];

    public ExerciseSessionOptionsSelectFlashcardsDto() {
        super(ExerciseSessionTypes.SELECT_FLASHCARD_DECK);
    }

    public boolean getInitiallyHideOptions() {
        return initiallyHideOptions;
    }

    public void setInitiallyHideOptions(boolean initiallyHideOptions) {
        this.initiallyHideOptions = initiallyHideOptions;
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
