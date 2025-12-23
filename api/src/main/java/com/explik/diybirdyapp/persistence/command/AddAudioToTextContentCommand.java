package com.explik.diybirdyapp.persistence.command;

/**
 * Command to add audio pronunciation to a text content vertex.
 * This is an atomic command that creates a pronunciation vertex and links it to the text content.
 */
public class AddAudioToTextContentCommand implements AtomicCommand {
    private String id;
    private String textContentId;
    private String audioUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextContentId() {
        return textContentId;
    }

    public void setTextContentId(String textContentId) {
        this.textContentId = textContentId;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
