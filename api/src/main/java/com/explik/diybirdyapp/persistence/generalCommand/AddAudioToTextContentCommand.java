package com.explik.diybirdyapp.persistence.generalCommand;

public class AddAudioToTextContentCommand {
    private final String textId;
    private final String audioUrl;

    public AddAudioToTextContentCommand(String textId, String audioUrl) {
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
