package com.explik.diybirdyapp.model;

public class WriteTranslatedSentenceExercise extends Exercise {
    private String originalSentence;
    private String targetLanguage;

    @Override
    public String getExerciseType() {
        return "write-translated-sentence-exercise";
    }

    public String getOriginalSentence() {
        return this.originalSentence;
    }

    public void setOriginalSentence(String originalSentence) {
        this.originalSentence = originalSentence;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
}
