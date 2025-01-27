package com.explik.diybirdyapp.controller.dto.exercise;

public class ExerciseContentTextDto extends ExerciseContentDto {
    private String text;
    private String pronunciationUrl;

    public ExerciseContentTextDto() {
        super(TYPE);
    }

    public static String TYPE = "text-content";

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getPronunciationUrl() { return pronunciationUrl; }

    public void setPronunciationUrl(String pronunciationUrl) { this.pronunciationUrl = pronunciationUrl; }
}
