package com.explik.diybirdyapp.manager.exerciseCreationManager;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;

public class ExerciseCreationContext {
    private ExerciseSessionDto sessionModel;
    private ExerciseSessionVertex sessionVertex;
    private FlashcardVertex flashcardVertex;
    private String flashcardSide;
    private PronunciationVertex pronunciationVertex;
    private TextContentVertex textContentVertex;
    private AbstractVertex basedOnContent;
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

    public PronunciationVertex getPronunciationVertex() {
        return pronunciationVertex;
    }

    public void setPronunciationVertex(PronunciationVertex pronunciationVertex) {
        this.pronunciationVertex = pronunciationVertex;
    }

    public TextContentVertex getTextContentVertex() {
        return textContentVertex;
    }

    public void setTextContentVertex(TextContentVertex textContentVertex) {
        this.textContentVertex = textContentVertex;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public AbstractVertex getBasedOnContent() {
        return basedOnContent;
    }

    public void setBasedOnContent(AbstractVertex basedOnContent) {
        this.basedOnContent = basedOnContent;
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

    public static ExerciseCreationContext createForPronunciation(
            ExerciseSessionVertex sessionVertex,
            PronunciationVertex pronunciationVertex,
            String exerciseType) {
        var context = new ExerciseCreationContext();
        context.setSessionVertex(sessionVertex);
        context.setPronunciationVertex(pronunciationVertex);
        context.setExerciseType(exerciseType);
        return context;
    }

    public static ExerciseCreationContext createForText(
            ExerciseSessionVertex sessionVertex,
            TextContentVertex textContentVertex,
            String exerciseType) {
        var context = new ExerciseCreationContext();
        context.setSessionVertex(sessionVertex);
        context.setTextContentVertex(textContentVertex);
        context.setExerciseType(exerciseType);
        return context;
    }
}
