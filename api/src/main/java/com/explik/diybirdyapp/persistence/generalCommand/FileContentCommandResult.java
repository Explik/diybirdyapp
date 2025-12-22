package com.explik.diybirdyapp.persistence.generalCommand;

public class FileContentCommandResult {
    private final byte[] content;
    private final String contentType;

    public FileContentCommandResult(byte[] content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }
}
