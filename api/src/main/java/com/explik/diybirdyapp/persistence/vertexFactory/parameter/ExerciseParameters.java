package com.explik.diybirdyapp.persistence.vertexFactory.parameter;

import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;

import java.util.UUID;

public class ExerciseParameters {
    private String id = UUID.randomUUID().toString();
    private ExerciseSessionVertex session;
    private String targetLanguage;

    private ExerciseContentParameters content;
    private ExerciseInputParametersSelectOptions selectOptionsInput;
    private ExerciseInputParametersWriteText writeTextInput;

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

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public ExerciseParameters withTargetLanguage(String language) {
        this.targetLanguage = language;
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

    public ExerciseInputParametersWriteText getWriteTextInput() {
        return writeTextInput;
    }

    public void setWriteTextInput(ExerciseInputParametersWriteText writeTextInput) {
        this.writeTextInput = writeTextInput;
    }

    public ExerciseParameters withWriteTextInput(ExerciseInputParametersWriteText writeTextInput) {
        this.writeTextInput = writeTextInput;
        return this;
    }
}
