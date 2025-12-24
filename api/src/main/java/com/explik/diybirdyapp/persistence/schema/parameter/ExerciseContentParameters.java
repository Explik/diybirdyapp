package com.explik.diybirdyapp.persistence.schema.parameter;

import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;

public class ExerciseContentParameters {
    private ContentVertex content;
    private String flashcardSide;

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

    public String getFlashcardSide() {
        return flashcardSide;
    }

    public void setFlashcardSide(String side) {
        this.flashcardSide = side;
    }

    public ExerciseContentParameters withFlashcardContent(FlashcardVertex content, String side) {
        this.content = content;
        this.flashcardSide = side;
        return this;
    }

}
