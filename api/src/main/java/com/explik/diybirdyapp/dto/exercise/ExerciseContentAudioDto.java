package com.explik.diybirdyapp.dto.exercise;

import jakarta.validation.constraints.NotNull;

public class ExerciseContentAudioDto extends ExerciseContentDto {
    @NotNull(message = "audioUrl.required")
    private String audioUrl;

    public ExerciseContentAudioDto() {
        super(TYPE);
    }

    public static String TYPE = "audio-content";

    public String getAudioUrl() { return audioUrl; }

    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}
