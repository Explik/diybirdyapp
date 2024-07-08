package com.explik.diybirdyapp.model.vertex;

import com.syncleus.ferma.annotations.Property;

public abstract class WrittenExerciseAnswerVertex extends ExerciseAnswerVertex {
    @Property("text")
    public abstract String getText();

    @Property("text")
    public abstract void setText(String text);
}
