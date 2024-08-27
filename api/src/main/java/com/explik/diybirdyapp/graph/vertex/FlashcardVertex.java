package com.explik.diybirdyapp.graph.vertex;

import com.explik.diybirdyapp.graph.model.ExerciseContentFlashcardModel;
import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Property;

public abstract class FlashcardVertex extends AbstractVertexFrame {
    @Property("id")
    public abstract String getId();

    @Property("id")
    public abstract void setId(String id);

    public FlashcardDeckVertex getDeck() {
        return traverse(g -> g.in("hasFlashcard")).nextOrDefaultExplicit(FlashcardDeckVertex.class, null);
    }

    public TextVertex getLeftContent() {
        return traverse(g -> g.out("hasLeftContent")).nextOrDefaultExplicit(TextVertex.class, null);
    }

    public void setLeftContent(TextVertex vertex) {
        var existingContent = getLeftContent();

        if (existingContent != null)
            existingContent.remove();

        addFramedEdgeExplicit("hasLeftContent", vertex);
    }

    public TextVertex getRightContent() {
        return traverse(g -> g.out("hasRightContent")).nextOrDefaultExplicit(TextVertex.class, null);
    }

    public void setRightContent(TextVertex vertex) {
        var existingContent = getRightContent();

        if (existingContent != null)
            existingContent.remove();

        addFramedEdgeExplicit("hasRightContent", vertex);
    }

    public FlashcardModel toFlashcardModel() {
        var leftContent = getLeftContent();
        var rightContent = getRightContent();

        var model = new FlashcardModel();
        model.setId(getId());
        model.setDeckId(getDeck().getId());
        model.setLeftValue(leftContent.getValue());
        model.setLeftLanguage(leftContent.getLanguage().toFlashcardLanguageModel());
        model.setRightValue(rightContent.getValue());
        model.setRightLanguage(rightContent.getLanguage().toFlashcardLanguageModel());
        return model;
    }

    public ExerciseContentFlashcardModel toExerciseContentFlashcardModel() {
        var model = new ExerciseContentFlashcardModel();
        model.setId(getId());
        model.setBack(getRightContent().toExerciseContentTextModel());
        model.setFront(getLeftContent().toExerciseContentTextModel());
        return model;
    }
}
