package com.explik.diybirdyapp.model.content;

import jakarta.validation.constraints.NotNull;

public class TextContentTranscriptionDto {
    @NotNull(message = "transcription.required")
    private String transcription;

    @NotNull(message = "transcriptionSystem.required")
    private String transcriptionSystem;

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getTranscriptionSystem() {
        return transcriptionSystem;
    }

    public void setTranscriptionSystem(String transcriptionSystem) {
        this.transcriptionSystem = transcriptionSystem;
    }
}
