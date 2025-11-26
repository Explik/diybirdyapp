package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsWriteFlashcardsDto extends ExerciseSessionOptionsDto {
    private String answerLanguageId;
    private ExerciseSessionOptionsLanguageOptionDto[] availableAnswerLanguages = new ExerciseSessionOptionsLanguageOptionDto[0];
    private boolean retypeCorrectAnswerEnabled;

    public ExerciseSessionOptionsWriteFlashcardsDto() {
        super(ExerciseSessionTypes.WRITE_FLASHCARD);
    }

    public boolean getRetypeCorrectAnswerEnabled() {
        return retypeCorrectAnswerEnabled;
    }

    public void setRetypeCorrectAnswerEnabled(boolean retypeCorrectAnswerEnabled) {
        this.retypeCorrectAnswerEnabled = retypeCorrectAnswerEnabled;
    }

    public String getAnswerLanguageId() {
        return answerLanguageId;
    }

    public void setAnswerLanguageId(String answerLanguageId) {
        this.answerLanguageId = answerLanguageId;
    }

    public ExerciseSessionOptionsLanguageOptionDto[] getAvailableAnswerLanguages() {
        return availableAnswerLanguages;
    }

    public void setAvailableAnswerLanguages(ExerciseSessionOptionsLanguageOptionDto[] availableAnswerLanguageIds) {
        this.availableAnswerLanguages = availableAnswerLanguageIds;
    }
}
