package com.explik.diybirdyapp.controller.dto.exercise;

import jakarta.validation.constraints.NotNull;

public class ExerciseInputRecognizabilityRatingDto extends ExerciseInputDto {
    @NotNull
    private String rating;

    public String getRating() { return rating; }

    public void setRating(String rating) { this.rating = rating; }
}
