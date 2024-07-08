package com.explik.diybirdyapp.model.dto;

import com.explik.diybirdyapp.model.WriteSentenceUsingWordExercise;

public class WriteSentenceUsingWordExerciseDTO extends ExerciseDTO implements WriteSentenceUsingWordExercise {
    String word;

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public void setWord(String word) {
        this.word = word;
    }
}
