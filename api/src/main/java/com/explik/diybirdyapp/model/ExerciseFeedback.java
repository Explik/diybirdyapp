package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.VertexType;
import com.explik.diybirdyapp.model.vertex.ExerciseVertex;

@VertexType(ExerciseVertex.class)
public interface ExerciseFeedback {
    public String getFeedbackType();
    public void setFeedbackType(String feedbackType);
}
