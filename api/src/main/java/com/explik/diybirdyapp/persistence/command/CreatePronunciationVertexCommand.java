package com.explik.diybirdyapp.persistence.command;

public class CreatePronunciationVertexCommand implements AtomicCommand {
    private String id;
    private String audioUrl;
    private String sourceVertexId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getSourceVertexId() {
        return sourceVertexId;
    }

    public void setSourceVertex(String sourceVertexId) {
        this.sourceVertexId = sourceVertexId;
    }
}
