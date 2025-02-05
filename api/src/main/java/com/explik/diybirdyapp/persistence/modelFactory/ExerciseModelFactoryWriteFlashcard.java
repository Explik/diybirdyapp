package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseContentFlashcardSideModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.WRITE_FLASHCARD + ComponentTypes.MODEL_FACTORY)
public class ExerciseModelFactoryWriteFlashcard implements ExerciseModelFactory {
    @Autowired
    ExerciseContentModelFactory contentModelFactory;

    @Override
    public ExerciseModel create(ExerciseVertex vertex) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");
        if (!vertex.getType().equals(ExerciseTypes.WRITE_FLASHCARD))
            throw new RuntimeException("Vertex type is not WRITE_FLASHCARD");

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());

        var flashcardSideContent = contentModelFactory.create(vertex.getContent());
        var flashcardSide = ExerciseContentFlashcardSideModel.create(flashcardSideContent);
        instance.setContent(flashcardSide);

        return instance;
    }
}
