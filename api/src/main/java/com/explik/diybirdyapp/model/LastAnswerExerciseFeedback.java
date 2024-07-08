package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.Discriminator;

@Discriminator("last-answer-exercise-feedback")
public interface LastAnswerExerciseFeedback extends ExerciseFeedback {
    public ExerciseAnswer getLastAnswer();
    public void setLastAnswer(ExerciseAnswer lastAnswer);
}
