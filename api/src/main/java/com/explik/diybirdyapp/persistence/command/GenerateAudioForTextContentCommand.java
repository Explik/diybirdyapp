package com.explik.diybirdyapp.persistence.command;

/**
 * Command to generate audio pronunciation for text content using text-to-speech.
 */
public class GenerateAudioForTextContentCommand implements AtomicCommand {
    private String textContentId;

    public String getTextContentId() {
        return textContentId;
    }

    public void setTextContentId(String textContentId) {
        this.textContentId = textContentId;
    }
}
