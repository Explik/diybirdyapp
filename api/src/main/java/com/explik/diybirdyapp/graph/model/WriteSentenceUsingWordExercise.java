package com.explik.diybirdyapp.graph.model;

import com.explik.diybirdyapp.annotations.ExerciseType;

@ExerciseType("write-sentence-using-word-exercise")
public class WriteSentenceUsingWordExercise extends Exercise<WrittenExerciseAnswer> {
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
