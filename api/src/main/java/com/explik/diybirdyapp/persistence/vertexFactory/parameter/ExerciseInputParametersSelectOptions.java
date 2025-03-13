package com.explik.diybirdyapp.persistence.vertexFactory.parameter;

import com.explik.diybirdyapp.persistence.vertex.ContentVertex;

import java.util.List;

public class ExerciseInputParametersSelectOptions {
    private List<ContentVertex> correctOptions;
    private List<ContentVertex> incorrectOptions;

    public List<ContentVertex> getCorrectOptions() {
        return correctOptions;
    }

    public void setCorrectOptions(List<ContentVertex> correctOptions) {
        this.correctOptions = correctOptions;
    }

    public ExerciseInputParametersSelectOptions withCorrectOptions(List<ContentVertex> correctOptions) {
        this.correctOptions = correctOptions;
        return this;
    }

    public List<ContentVertex> getIncorrectOptions() {
        return incorrectOptions;
    }

    public void setIncorrectOptions(List<ContentVertex> incorrectOptions) {
        this.incorrectOptions = incorrectOptions;
    }

    public ExerciseInputParametersSelectOptions withIncorrectOptions(List<ContentVertex> incorrectOptions) {
        this.incorrectOptions = incorrectOptions;
        return this;
    }
}
