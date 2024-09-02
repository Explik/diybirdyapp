package com.explik.diybirdyapp.graph.vertex;

import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Property;

public abstract class ExerciseVertex extends AbstractVertexFrame {
    @Property("id")
    public abstract String getId();

    @Property("id")
    public abstract void setId(String id);

    @Property("exerciseType")
    public abstract String getType();

    @Property("exerciseType")
    public abstract void setType(String type);

    public TextVertex getTextContent() {
        return traverse(g -> g.out("hasContent")).nextExplicit(TextVertex.class);
    }

    public FlashcardVertex getFlashcardContent() {
        return traverse(g -> g.out("hasContent")).nextExplicit(FlashcardVertex.class);
    }

    public ExerciseModel toLimitedExerciseModel() {
        var model = new ExerciseModel();
        model.setId(getId());
        model.setType(getType());
        return model;
    }
}
