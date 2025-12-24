package com.explik.diybirdyapp.persistence.command;

import java.util.List;

public class CreateWriteFlashcardSessionCommand implements AtomicCommand {
    private String id;
    private String flashcardDeckId;
    private Boolean textToSpeechEnabled;
    private List<String> answerLanguageIds;
    private Boolean retypeCorrectAnswer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlashcardDeckId() {
        return flashcardDeckId;
    }

    public void setFlashcardDeckId(String flashcardDeckId) {
        this.flashcardDeckId = flashcardDeckId;
    }

    public Boolean getTextToSpeechEnabled() {
        return textToSpeechEnabled;
    }

    public void setTextToSpeechEnabled(Boolean textToSpeechEnabled) {
        this.textToSpeechEnabled = textToSpeechEnabled;
    }

    public List<String> getAnswerLanguageIds() {
        return answerLanguageIds;
    }

    public void setAnswerLanguageIds(List<String> answerLanguageIds) {
        this.answerLanguageIds = answerLanguageIds;
    }

    public Boolean getRetypeCorrectAnswer() {
        return retypeCorrectAnswer;
    }

    public void setRetypeCorrectAnswer(Boolean retypeCorrectAnswer) {
        this.retypeCorrectAnswer = retypeCorrectAnswer;
    }
}
