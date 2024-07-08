package com.explik.diybirdyapp.model;

import com.explik.diybirdyapp.annotations.Discriminator;
import com.explik.diybirdyapp.annotations.DtoType;
import com.explik.diybirdyapp.annotations.VertexType;
import com.explik.diybirdyapp.model.dto.WriteSentenceUsingWordExerciseDTO;
import com.explik.diybirdyapp.model.vertex.WriteSentenceUsingWordExerciseVertex;

@Discriminator("write-sentence-using-word-exercise")
@DtoType(WriteSentenceUsingWordExerciseDTO.class)
@VertexType(WriteSentenceUsingWordExerciseVertex.class)
public interface WriteSentenceUsingWordExercise extends Exercise {
    public String getWord();
    public void setWord(String word);
}
