package com.explik.diybirdyapp.persistence.command;

public class AddAudioForTextCommand {
    private final String textId;
    private final String audioUrl;

    public AddAudioForTextCommand(String textId, String audioUrl) {
        this.textId = textId;
        this.audioUrl = audioUrl;
    }

    public String getTextId() {
        return textId;
    }

    public String getAudioUrl() {
        return audioUrl;
    }
}
