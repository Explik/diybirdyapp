package com.explik.diybirdyapp.model.exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseInputSelectOptionsModel extends ExerciseInputModel {
    private Feedback feedback;
    private List<BaseOption> options = new ArrayList<>();
    private String value;

    public Feedback getFeedback() { return feedback; }

    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public List<BaseOption> getOptions() { return options; }

    public void addOption(BaseOption option) { options.add(option); }

    public void setOptions(List<BaseOption> options) { this.options = options; }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    public static abstract class BaseOption {
        private String id;

        public BaseOption() {
        }

        public BaseOption(String id) {
            this.id = id;
        }

        public String getId() { return id; }

        public void setId(String id) { this.id = id; }
    }

    public static class AudioOption extends BaseOption {
        private String url;

        public AudioOption() {
            super();
        }

        public AudioOption(String id, String url) {
            super(id);
            this.url = url;
        }

        public String getUrl() { return url; }

        public void setUrl(String url) { this.url = url; }
    }

    public static class TextOption extends BaseOption {
        private String text;

        public TextOption() {
            super();
        }

        public TextOption(String id, String text) {
            super(id);
            this.text = text;
        }

        public String getText() { return text; }

        public void setText(String text) { this.text = text; }
    }

    public static class ImageOption extends BaseOption {
        private String url;

        public ImageOption() {
            super();
        }

        public ImageOption(String id, String url) {
            super(id);
            this.url = url;
        }

        public String getUrl() { return url; }

        public void setUrl(String url) { this.url = url; }
    }

    public static class VideoOption extends BaseOption {
        private String url;

        public VideoOption() {
            super();
        }

        public VideoOption(String id, String url) {
            super(id);
            this.url = url;
        }

        public String getUrl() { return url; }

        public void setUrl(String url) { this.url = url; }
    }

    public static class Feedback {
        private List<String> correctOptionIds;
        private List<String> incorrectOptionIds;

        public List<String> getCorrectOptionIds() { return correctOptionIds; }

        public void setCorrectOptionIds(List<String> correctOptionIds) { this.correctOptionIds = correctOptionIds; }

        public List<String> getIncorrectOptionIds() { return incorrectOptionIds; }

        public void setIncorrectOptionIds(List<String> incorrectOptionIds) { this.incorrectOptionIds = incorrectOptionIds; }
    }
}
