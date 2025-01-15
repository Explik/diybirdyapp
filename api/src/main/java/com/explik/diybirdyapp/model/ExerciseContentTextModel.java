package com.explik.diybirdyapp.model;

public class ExerciseContentTextModel extends ExerciseContentModel {
    private String text;
    private String pronunciationUrl;

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getPronunciationUrl() { return pronunciationUrl; }

    public void setPronunciationUrl(String pronunciationUrl) { this.pronunciationUrl = pronunciationUrl; }
}
