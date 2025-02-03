package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.SELECT_FLASHCARD + ComponentTypes.MODEL_FACTORY)
public class ExerciseModelFactorySelectFlashcard implements ExerciseModelFactory {
    @Autowired
    ExerciseContentModelFactory contentModelFactory;

    @Autowired
    ExerciseInputModelFactoryMultipleChoice inputModelFactory;

    @Override
    public ExerciseModel create(ExerciseVertex vertex) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");
        if (!vertex.getType().equals(ExerciseTypes.SELECT_FLASHCARD))
            throw new RuntimeException("Vertex type is not SELECT_FLASHCARD");

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());

        // Add content
        var contentModel = contentModelFactory.create(vertex);
        instance.setContent(contentModel);

        // Add input
        var inputModel = inputModelFactory.create(vertex);
        instance.setInput(inputModel);

        return instance;
    }
}
