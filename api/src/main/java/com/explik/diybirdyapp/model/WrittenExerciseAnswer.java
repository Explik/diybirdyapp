package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.Discriminator;

@Discriminator("written-exercise-answer")
public interface WrittenExerciseAnswer extends ExerciseAnswer {
    public String getText();
    public void setText(String text);
}
