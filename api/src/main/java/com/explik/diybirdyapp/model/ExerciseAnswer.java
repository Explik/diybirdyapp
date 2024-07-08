package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.DtoType;
import com.explik.diybirdyapp.annotations.VertexType;
import com.explik.diybirdyapp.model.vertex.ExerciseAnswerVertex;

@VertexType(ExerciseAnswerVertex.class)
public interface ExerciseAnswer {
    public String getId();
    public void setId(String id);

    public String getAnswerType();
    public void setAnswerType(String answerType);
}