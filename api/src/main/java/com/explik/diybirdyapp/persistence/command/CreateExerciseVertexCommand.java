package com.explik.diybirdyapp.persistence.command;

import java.util.List;

public class CreateExerciseVertexCommand implements CompositeCommand, AtomicCommand {
    private String id;
    private String exerciseTypeId;
    private String sessionId;
    private String targetLanguageId;
    private String contentId;
    private String flashcardId;
    private String flashcardSide;
    private List<String> correctOptionIds;
    private List<String> incorrectOptionIds;
    private List<String> arrangeTextOptionIds;
    private List<CreatePairVertexCommand> pairCommands;
    
    // Getters and setters
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

    public List<String> getCorrectOptionIds() {
        return correctOptionIds;
    }

    public void setCorrectOptionIds(List<String> correctOptionIds) {
        this.correctOptionIds = correctOptionIds;
    }

    public List<String> getIncorrectOptionIds() {
        return incorrectOptionIds;
    }

    public void setIncorrectOptionIds(List<String> incorrectOptionIds) {
        this.incorrectOptionIds = incorrectOptionIds;
    }

    public List<String> getArrangeTextOptionIds() {
        return arrangeTextOptionIds;
    }

    public void setArrangeTextOptionIds(List<String> arrangeTextOptionIds) {
        this.arrangeTextOptionIds = arrangeTextOptionIds;
    }

    public List<CreatePairVertexCommand> getPairCommands() {
        return pairCommands;
    }

    public void setPairCommands(List<CreatePairVertexCommand> pairCommands) {
        this.pairCommands = pairCommands;
    }

    @Override
    public List<AtomicCommand> getCommands() {
        var commands = new java.util.ArrayList<AtomicCommand>();
        if (pairCommands != null) {
            commands.addAll(pairCommands);
        }
        return commands;
    }
}
