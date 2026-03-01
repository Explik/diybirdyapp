package com.explik.diybirdyapp.model.exercise;

import jakarta.validation.constraints.NotNull;

/**
 * Input DTO for the sort-flashcard exercise.
 * The user places a flashcard into either the "know" or "dont-know" pile.
 */
public class ExerciseInputSortOptionsDto extends ExerciseInputDto {
    @NotNull(message = "pile.required")
    private String pile;

    public String getPile() { return pile; }

    public void setPile(String pile) { this.pile = pile; }
}
