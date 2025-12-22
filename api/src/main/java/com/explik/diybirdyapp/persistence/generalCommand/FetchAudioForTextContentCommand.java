package com.explik.diybirdyapp.persistence.generalCommand;

public class FetchAudioForTextContentCommand {
    private final String textId;

    public FetchAudioForTextContentCommand(String textId) {
        this.textId = textId;
    }

    public String getTextId() {
        return textId;
    }
}
