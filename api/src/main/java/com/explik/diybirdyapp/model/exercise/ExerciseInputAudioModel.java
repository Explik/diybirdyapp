package com.explik.diybirdyapp.model.exercise;

public class ExerciseInputAudioModel extends ExerciseInputModel {
    private String url;
    private String transcription;

    public String getUrl() { return url; }

    public void setUrl(String audio) { this.url = audio; }

    public String getTranscription() { return transcription; }

    public void setTranscription(String transcription) { this.transcription = transcription; }
}
