package com.explik.diybirdyapp.controller.dto.exercise;

public class ExerciseContentAudioDto extends ExerciseContentDto {
    private String audioUrl;

    public ExerciseContentAudioDto() {
        super(TYPE);
    }

    public static String TYPE = "audio-content";

    public String getAudioUrl() { return audioUrl; }

    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}
