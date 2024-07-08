package com.explik.diybirdyapp.model.vertex;

import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Property;

public abstract class ExerciseLightVertex extends AbstractVertexFrame {
    @Property("id")
    public abstract String getId();

    @Property("id")
    public abstract void setId(String id);

    @Property("exerciseType")
    public abstract String getExerciseType();

    @Property("exerciseType")
    public abstract void setExerciseType(String exerciseType);
}
