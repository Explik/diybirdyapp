package com.explik.diybirdyapp.model.vertex;

import com.explik.diybirdyapp.model.Exercise;
import com.explik.diybirdyapp.model.ExerciseAnswer;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.Property;
import com.syncleus.ferma.AbstractVertexFrame;
import org.apache.tinkerpop.gremlin.structure.Direction;

import java.util.List;

public abstract class ExerciseVertex extends AbstractVertexFrame implements Exercise {
    @Property("id")
    public abstract String getId();

    @Property("id")
    public abstract void setId(String id);

    @Property("exerciseType")
    public abstract String getExerciseType();

    @Property("exerciseType")
    public abstract void setExerciseType(String exerciseType);

    @Adjacency(label = "basedOn", direction = Direction.IN)
    public abstract List<ExerciseAnswerVertex> getAnswers();
}