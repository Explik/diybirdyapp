package com.explik.diybirdyapp.model.content;

/**
 * Model representing audio file content with its data and content type.
 */
public class AudioFileModel {
    private final byte[] data;
    private final String contentType;

    public AudioFileModel(byte[] data, String contentType) {
        this.data = data;
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }
}
