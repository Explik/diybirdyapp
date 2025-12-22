package com.explik.diybirdyapp.persistence.generalCommand;

public class GenerateAudioForTextContentCommand {
    private String textContentId;

    public GenerateAudioForTextContentCommand(String textContentId) {
        this.textContentId = textContentId;
    }

    public String getTextContentId() {
        return textContentId;
    }
}
