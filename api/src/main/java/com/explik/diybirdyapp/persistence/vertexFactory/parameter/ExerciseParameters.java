package com.explik.diybirdyapp.persistence.vertexFactory.parameter;

import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;

import java.util.UUID;

public class ExerciseParameters {
    private String id = UUID.randomUUID().toString();
    private ExerciseSessionVertex session;
    private ExerciseContentParameters content;
    private ExerciseInputParametersSelectOptions selectOptionsInput;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExerciseParameters withId(String id) {
        this.id = id;
        return this;
    }

    public ExerciseSessionVertex getSession() {
        return session;
    }

    public void setSession(ExerciseSessionVertex session) {
        this.session = session;
    }

    public ExerciseParameters withSession(ExerciseSessionVertex session) {
        this.session = session;
        return this;
    }

    public ExerciseContentParameters getContent() {
        return content;
    }

    public void setContent(ExerciseContentParameters content) {
        this.content = content;
    }

    public ExerciseParameters withContent(ExerciseContentParameters content) {
        this.content = content;
        return this;
    }

    public ExerciseInputParametersSelectOptions getSelectOptionsInput() {
        return selectOptionsInput;
    }

    public void setSelectOptionsInput(ExerciseInputParametersSelectOptions selectOptionsInput) {
        this.selectOptionsInput = selectOptionsInput;
    }

    public ExerciseParameters withSelectOptionsInput(ExerciseInputParametersSelectOptions selectOptionsInput) {
        this.selectOptionsInput = selectOptionsInput;
        return this;
    }
}
