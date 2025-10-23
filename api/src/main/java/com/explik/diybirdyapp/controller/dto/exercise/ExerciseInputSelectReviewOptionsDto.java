package com.explik.diybirdyapp.controller.dto.exercise;

import jakarta.validation.constraints.NotNull;

public class ExerciseInputSelectReviewOptionsDto extends ExerciseInputDto {
    @NotNull(message = "rating.required")
    private String rating;

    public String getRating() { return rating; }

    public void setRating(String rating) { this.rating = rating; }
}
