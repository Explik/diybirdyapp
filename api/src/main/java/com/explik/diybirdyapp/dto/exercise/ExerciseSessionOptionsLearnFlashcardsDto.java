package com.explik.diybirdyapp.dto.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsLearnFlashcardsDto extends ExerciseSessionOptionsDto {
    private String[] answerLanguageIds = new String[0];
    private String[] availableAnswerLanguageIds = new String[0];
    private boolean retypeCorrectAnswerEnabled;

    public ExerciseSessionOptionsLearnFlashcardsDto() {
        super(ExerciseSessionTypes.LEARN_FLASHCARD);
    }

    public String[] getAnswerLanguageIds() {
        return answerLanguageIds;
    }

    public void setAnswerLanguageIds(String[] answerLanguageIds) {
        this.answerLanguageIds = answerLanguageIds;
    }

    public String[] getAvailableAnswerLanguageIds() {
        return availableAnswerLanguageIds;
    }

    public void setAvailableAnswerLanguageIds(String[] availableAnswerLanguageIds) {
        this.availableAnswerLanguageIds = availableAnswerLanguageIds;
    }

    public boolean getRetypeCorrectAnswerEnabled() {
        return retypeCorrectAnswerEnabled;
    }

    public void setRetypeCorrectAnswerEnabled(boolean retypeCorrectAnswerEnabled) {
        this.retypeCorrectAnswerEnabled = retypeCorrectAnswerEnabled;
    }
}
