package com.explik.diybirdyapp.persistence.command;

public class GenerateAudioForTextContentCommand {
    private String textContentId;

    public GenerateAudioForTextContentCommand(String textContentId) {
        this.textContentId = textContentId;
    }

    public String getTextContentId() {
        return textContentId;
    }
}
