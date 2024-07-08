package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.Discriminator;

@Discriminator("multiple-choice-exercise-answer")
public interface MultipleChoiceExerciseAnswer extends ExerciseAnswer {
    public String getOptionId();
    public void setOptionId(String optionId);
}
