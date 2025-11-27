package com.explik.diybirdyapp.persistence.vertexFactory.parameter;

import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;

public class ExerciseInputParametersRecordAudio {
    private TextContentVertex correctOption;

    public ContentVertex getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(TextContentVertex correctOption) {
        this.correctOption = correctOption;
    }

    public ExerciseInputParametersRecordAudio withCorrectOption(TextContentVertex correctOption) {
        this.correctOption = correctOption;
        return this;
    }
}
