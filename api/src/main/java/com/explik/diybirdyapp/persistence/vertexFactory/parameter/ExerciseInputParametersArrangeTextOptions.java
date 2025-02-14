package com.explik.diybirdyapp.persistence.vertexFactory.parameter;

import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;

import java.util.List;

public class ExerciseInputParametersArrangeTextOptions {
    private List<TextContentVertex> options;

    public List<TextContentVertex> getOptions() {
        return options;
    }

    public void setOptions(List<TextContentVertex> options) {
        this.options = options;
    }

    public ExerciseInputParametersArrangeTextOptions withOptions(List<TextContentVertex> options) {
        this.options = options;
        return this;
    }
}
