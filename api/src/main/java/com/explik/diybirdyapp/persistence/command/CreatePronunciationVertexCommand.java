package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;

public class CreatePronunciationVertexCommand implements AtomicCommand {
    private String id;
    private TextContentVertex sourceVertex;
    private AudioContentVertex audioContentVertex;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TextContentVertex getSourceVertex() {
        return sourceVertex;
    }

    public void setSourceVertex(TextContentVertex sourceVertex) {
        this.sourceVertex = sourceVertex;
    }

    public AudioContentVertex getAudioContentVertex() {
        return audioContentVertex;
    }

    public void setAudioContentVertex(AudioContentVertex audioContentVertex) {
        this.audioContentVertex = audioContentVertex;
    }
}
