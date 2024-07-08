package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.DtoType;
import com.explik.diybirdyapp.annotations.VertexType;
import com.explik.diybirdyapp.model.dto.ExerciseDTO;
import com.explik.diybirdyapp.model.vertex.ExerciseVertex;

@DtoType(ExerciseDTO.class)
@VertexType(ExerciseVertex.class)
public interface Exercise {
    public String getId();
    public void setId(String id);

    public String getExerciseType();
    public void setExerciseType(String exerciseType);
}