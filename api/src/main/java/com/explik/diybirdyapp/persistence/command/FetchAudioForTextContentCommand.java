package com.explik.diybirdyapp.persistence.command;

public class FetchAudioForTextContentCommand {
    private final String textId;

    public FetchAudioForTextContentCommand(String textId) {
        this.textId = textId;
    }

    public String getTextId() {
        return textId;
    }
}
