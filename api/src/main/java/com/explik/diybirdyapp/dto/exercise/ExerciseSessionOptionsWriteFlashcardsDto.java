package com.explik.diybirdyapp.dto.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsWriteFlashcardsDto extends ExerciseSessionOptionsDto {
    private String answerLanguageId;
    private String[] availableAnswerLanguageIds = new String[0];
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

    public String[] getAvailableAnswerLanguageIds() {
        return availableAnswerLanguageIds;
    }

    public void setAvailableAnswerLanguageIds(String[] availableAnswerLanguageIds) {
        this.availableAnswerLanguageIds = availableAnswerLanguageIds;
    }
}
