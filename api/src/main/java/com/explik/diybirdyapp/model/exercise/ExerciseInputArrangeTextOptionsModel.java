package com.explik.diybirdyapp.model.exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseInputArrangeTextOptionsModel extends ExerciseInputModel {
    private List<Option> options = new ArrayList<>();

    public List<Option> getOptions() {
        return options;
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public static class Option {
        private String id;
        private String text;

        public Option() {

        }

        public Option(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public String getId() { return id; }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
