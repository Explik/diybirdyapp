package com.explik.diybirdyapp.graph.vertex;

import com.explik.diybirdyapp.graph.model.ExerciseContentTextModel;
import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Property;

public abstract class TextVertex extends AbstractVertexFrame {
    @Property("id")
    public abstract String getId();

    @Property("id")
    public abstract void setId(String id);

    @Property("value")
    public abstract String getValue();

    @Property("value")
    public abstract void setValue(String value);

    public FlashcardLanguageVertex getLanguage() {
        return traverse(g -> g.out("hasLanguage")).nextExplicit(FlashcardLanguageVertex.class);
    }

    public void setLanguage(FlashcardLanguageVertex vertex) {
        // TODO Remove existing language if any
        addFramedEdgeExplicit("hasLanguage", vertex);
    }

    public ExerciseContentTextModel toExerciseContentTextModel() {
        var model = new ExerciseContentTextModel();
        model.setId(getId());
        model.setText(getValue());
        return model;
    }
}
