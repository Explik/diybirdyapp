package com.explik.diybirdyapp.controller.dto.exercise;

import jakarta.validation.constraints.NotNull;

public class ExerciseInputAudioDto extends ExerciseInputDto {
    @NotNull
    private String url;

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
}
