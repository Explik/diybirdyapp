package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;

public class ExerciseCreationContext {
    private ExerciseSessionDto sessionModel;
    private ExerciseSessionVertex sessionVertex;
    private FlashcardVertex flashcardVertex;
    private String flashcardSide;
    private String exerciseType;

    public ExerciseSessionDto getSessionModel() {
        return sessionModel;
    }

    public void setSessionModel(ExerciseSessionDto sessionModel) {
        this.sessionModel = sessionModel;
    }

    public ExerciseSessionVertex getSessionVertex() {
        return sessionVertex;
    }

    public void setSessionVertex(ExerciseSessionVertex sessionVertex) {
        this.sessionVertex = sessionVertex;
    }

    public FlashcardVertex getFlashcardVertex() {
        return flashcardVertex;
    }

    public void setFlashcardVertex(FlashcardVertex flashcardVertex) {
        this.flashcardVertex = flashcardVertex;
    }

    public String getFlashcardSide() {
        return flashcardSide;
    }

    public void setFlashcardSide(String flashcardSide) {
        this.flashcardSide = flashcardSide;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public static ExerciseCreationContext createDefault(ExerciseSessionDto sessionModel) {
        var context = new ExerciseCreationContext();
        context.setSessionModel(sessionModel);
        return context;
    }

    public static ExerciseCreationContext createForFlashcard(
            ExerciseSessionVertex sessionVertex, 
            FlashcardVertex flashcardVertex, 
            String flashcardSide,
            String exerciseType) {
        var context = new ExerciseCreationContext();
        context.setSessionVertex(sessionVertex);
        context.setFlashcardVertex(flashcardVertex);
        context.setFlashcardSide(flashcardSide);
        context.setExerciseType(exerciseType);
        return context;
    }
}
