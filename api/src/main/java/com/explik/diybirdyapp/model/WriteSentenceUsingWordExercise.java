package com.explik.diybirdyapp.model;

public class WriteSentenceUsingWordExercise extends Exercise {
    private String word;

    @Override
    public String getExerciseType() {
        return "write-sentence-using-word-exercise";
    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
