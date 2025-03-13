package com.explik.diybirdyapp.persistence.vertexFactory.parameter;

import com.explik.diybirdyapp.persistence.vertex.ContentVertex;

public class ExerciseContentParameters {
    private ContentVertex content;

    public ContentVertex getVertex() {
        return content;
    }

    public void setContent(ContentVertex content) {
        this.content = content;
    }

    public ExerciseContentParameters withContent(ContentVertex content) {
        this.content = content;
        return this;
    }
}
