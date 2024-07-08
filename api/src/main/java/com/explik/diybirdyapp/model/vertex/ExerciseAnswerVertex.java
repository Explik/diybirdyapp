package com.explik.diybirdyapp.model.vertex;

import com.explik.diybirdyapp.model.ExerciseAnswer;
import com.syncleus.ferma.annotations.Adjacency;
import com.syncleus.ferma.annotations.Property;
import com.syncleus.ferma.AbstractVertexFrame;

import java.util.List;

public abstract class ExerciseAnswerVertex extends AbstractVertexFrame implements ExerciseAnswer {
    @Property("id")
    public abstract String getId();

    @Property("id")
    public abstract void setId(String id);

    @Property("answerType")
    public abstract String getAnswerType();

    @Property("answerType")
    public abstract void setAnswerType(String answerType);

    @Adjacency(label = "hasFeedback")
    public abstract List<ExerciseAnswerVertex> getFeedback();
}