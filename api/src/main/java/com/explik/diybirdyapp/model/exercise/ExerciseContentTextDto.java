package com.explik.diybirdyapp.model.exercise;

public class ExerciseContentTextDto extends ExerciseContentDto {
    private String text;
    private String pronunciationUrl;
    private String transcription;

    public ExerciseContentTextDto() {
        super(TYPE);
    }

    public static String TYPE = "text-content";

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getPronunciationUrl() { return pronunciationUrl; }

    public void setPronunciationUrl(String pronunciationUrl) { this.pronunciationUrl = pronunciationUrl; }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }
}
