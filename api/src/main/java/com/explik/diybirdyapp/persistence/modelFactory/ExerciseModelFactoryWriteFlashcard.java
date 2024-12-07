package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.WRITE_FLASHCARD + ComponentTypes.MODEL_FACTORY)
public class ExerciseModelFactoryWriteFlashcard implements ExerciseModelFactory {
    @Override
    public ExerciseModel create(ExerciseVertex vertex) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");
        if (!vertex.getType().equals(ExerciseTypes.WRITE_FLASHCARD))
            throw new RuntimeException("Vertex type is not WRITE_FLASHCARD");

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());

        if (vertex.getFlashcardSide().equals("front"))
            instance.setContent(vertex.getFlashcardContent().toExerciseContentFlashcardModel(true, false));
        else
            instance.setContent(vertex.getFlashcardContent().toExerciseContentFlashcardModel(false, true));

        return instance;
    }
}
