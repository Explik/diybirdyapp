package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;

public class CreatePronunciationVertexCommand implements AtomicCommand {
    private String id;
    private String audioUrl;
    private TextContentVertex sourceVertex;

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

    public TextContentVertex getSourceVertex() {
        return sourceVertex;
    }

    public void setSourceVertex(TextContentVertex sourceVertex) {
        this.sourceVertex = sourceVertex;
    }
}
