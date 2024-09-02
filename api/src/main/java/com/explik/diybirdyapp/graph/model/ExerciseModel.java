package com.explik.diybirdyapp.graph.model;

import java.util.Dictionary;
import java.util.Hashtable;

public class ExerciseModel {
    private String id;
    private String type;
    private ExerciseContentModel content;
    private ExerciseInputModel input;
    private Dictionary<String, Object> properties = new Hashtable<>();

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ExerciseContentModel getContent() {
        return content;
    }

    public void setContent(ExerciseContentModel content) {
        this.content = content;
    }

    public ExerciseInputModel getInput() {
        return input;
    }

    public void setInput(ExerciseInputModel input) {
        this.input = input;
    }

    public Dictionary<String, Object> getProperties() {
        return properties;
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public void setProperties(Dictionary<String, Object> properties) {
        this.properties = properties;
    }
}
