package com.explik.diybirdyapp.persistence.command;

public class CreateTranscriptionCommand implements AtomicCommand {
    private String id;
    private String sourceContentId;
    private String textValue;
    private String transcriptionSystemId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceContentId() {
        return sourceContentId;
    }

    public void setSourceContentId(String sourceContentId) {
        this.sourceContentId = sourceContentId;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getTranscriptionSystemId() {
        return transcriptionSystemId;
    }

    public void setTranscriptionSystemId(String transcriptionSystemId) {
        this.transcriptionSystemId = transcriptionSystemId;
    }
}
