package com.explik.diybirdyapp.graph.vertex;

import java.util.List;

public abstract class ExerciseMultipleChoiceVertex extends ExerciseVertex {
    public List<? extends TextVertex> getChoices() {
        return this.traverse(g -> g.out("hasChoice")).toListExplicit(TextVertex.class);
    }

    public TextVertex getCorrectChoice() {
        return this.traverse(g -> g.out("hasCorrectChoice")).nextExplicit(TextVertex.class);
    }
}
