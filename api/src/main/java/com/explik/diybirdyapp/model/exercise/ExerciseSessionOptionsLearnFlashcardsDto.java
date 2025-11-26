package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseSessionTypes;

public class ExerciseSessionOptionsLearnFlashcardsDto extends ExerciseSessionOptionsDto {
    private String[] answerLanguageIds = new String[0];
    private ExerciseSessionOptionsLanguageOptionDto[] availableAnswerLanguage = new ExerciseSessionOptionsLanguageOptionDto[0];
    private String[] exerciseTypesIds = new String[0];
    private ExerciseTypeOption[] availableExerciseTypes = new ExerciseTypeOption[0];

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

    public ExerciseSessionOptionsLanguageOptionDto[] getAvailableAnswerLanguages() {
        return availableAnswerLanguage;
    }

    public void setAvailableAnswerLanguages(ExerciseSessionOptionsLanguageOptionDto[] availableAnswerLanguageIds) {
        this.availableAnswerLanguage = availableAnswerLanguageIds;
    }

    public String[] getExerciseTypesIds() {
        return exerciseTypesIds;
    }

    public void setExerciseTypesIds(String[] exerciseTypesIds) {
        this.exerciseTypesIds = exerciseTypesIds;
    }

    public ExerciseTypeOption[] getAvailableExerciseTypes() {
        return availableExerciseTypes;
    }

    public void setAvailableExerciseTypes(ExerciseTypeOption[] availableExerciseTypes) {
        this.availableExerciseTypes = availableExerciseTypes;
    }

    public boolean getRetypeCorrectAnswerEnabled() {
        return retypeCorrectAnswerEnabled;
    }

    public void setRetypeCorrectAnswerEnabled(boolean retypeCorrectAnswerEnabled) {
        this.retypeCorrectAnswerEnabled = retypeCorrectAnswerEnabled;
    }

    public record ExerciseTypeOption(String id, String name) { }
}
