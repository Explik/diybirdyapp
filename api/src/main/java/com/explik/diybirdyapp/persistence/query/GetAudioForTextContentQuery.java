package com.explik.diybirdyapp.persistence.query;

/**
 * Query to fetch audio pronunciation for a text content vertex.
 * Returns the audio file data and content type if pronunciation exists.
 */
public class GetAudioForTextContentQuery {
    private String textContentId;

    public String getTextContentId() {
        return textContentId;
    }

    public void setTextContentId(String textContentId) {
        this.textContentId = textContentId;
    }
}
