package com.explik.diybirdyapp.persistence.vertexFactory.parameter;

import com.explik.diybirdyapp.persistence.vertex.ContentVertex;

public class ExerciseInputParametersWriteText {
    private ContentVertex correctOption;

    public ContentVertex getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(ContentVertex correctOption) {
        this.correctOption = correctOption;
    }

    public ExerciseInputParametersWriteText withCorrectOption(ContentVertex correctOption) {
        this.correctOption = correctOption;
        return this;
    }
}
