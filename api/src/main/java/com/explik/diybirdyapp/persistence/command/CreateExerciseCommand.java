package com.explik.diybirdyapp.persistence.command;

/**
 * Domain command to create a complete exercise.
 * This represents the business operation of creating an exercise from the domain perspective.
 */
public class CreateExerciseCommand implements AtomicCommand {
    private String id;
    private String exerciseTypeId;
    private String sessionId;
    private String targetLanguageId;
    
    // Content information
    private String contentId;
    private String flashcardId;
    private String flashcardSide;
    
    // Input commands - only one should be set based on exercise type
    private CreateArrangeTextOptionsInputCommand arrangeTextOptionsInput;
    private CreateSelectOptionsInputCommand selectOptionsInput;
    private CreatePairOptionsInputCommand pairOptionsInput;
    private CreateWriteTextInputCommand writeTextInput;
    private CreateRecordAudioInputCommand recordAudioInput;
    private CreateRecognizabilityRatingInputCommand recognizabilityRatingInput;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExerciseTypeId() {
        return exerciseTypeId;
    }

    public void setExerciseTypeId(String exerciseTypeId) {
        this.exerciseTypeId = exerciseTypeId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTargetLanguageId() {
        return targetLanguageId;
    }

    public void setTargetLanguageId(String targetLanguageId) {
        this.targetLanguageId = targetLanguageId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(String flashcardId) {
        this.flashcardId = flashcardId;
    }

    public String getFlashcardSide() {
        return flashcardSide;
    }

    public void setFlashcardSide(String flashcardSide) {
        this.flashcardSide = flashcardSide;
    }

    public CreateArrangeTextOptionsInputCommand getArrangeTextOptionsInput() {
        return arrangeTextOptionsInput;
    }

    public void setArrangeTextOptionsInput(CreateArrangeTextOptionsInputCommand arrangeTextOptionsInput) {
        this.arrangeTextOptionsInput = arrangeTextOptionsInput;
    }

    public CreateSelectOptionsInputCommand getSelectOptionsInput() {
        return selectOptionsInput;
    }

    public void setSelectOptionsInput(CreateSelectOptionsInputCommand selectOptionsInput) {
        this.selectOptionsInput = selectOptionsInput;
    }

    public CreatePairOptionsInputCommand getPairOptionsInput() {
        return pairOptionsInput;
    }

    public void setPairOptionsInput(CreatePairOptionsInputCommand pairOptionsInput) {
        this.pairOptionsInput = pairOptionsInput;
    }

    public CreateWriteTextInputCommand getWriteTextInput() {
        return writeTextInput;
    }

    public void setWriteTextInput(CreateWriteTextInputCommand writeTextInput) {
        this.writeTextInput = writeTextInput;
    }

    public CreateRecordAudioInputCommand getRecordAudioInput() {
        return recordAudioInput;
    }

    public void setRecordAudioInput(CreateRecordAudioInputCommand recordAudioInput) {
        this.recordAudioInput = recordAudioInput;
    }

    public CreateRecognizabilityRatingInputCommand getRecognizabilityRatingInput() {
        return recognizabilityRatingInput;
    }

    public void setRecognizabilityRatingInput(CreateRecognizabilityRatingInputCommand recognizabilityRatingInput) {
        this.recognizabilityRatingInput = recognizabilityRatingInput;
    }
}
