package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.SELECT_FLASHCARD + ComponentTypes.MODEL_FACTORY)
public class ExerciseModelFactorySelectFlashcard implements ExerciseModelFactory {
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
        if(vertex.getFlashcardSide().equals("front"))
            instance.setContent(vertex.getFlashcardContent().toExerciseContentFlashcardModel(true, false));
        else
            instance.setContent(vertex.getFlashcardContent().toExerciseContentFlashcardModel(false, true));

        // Add input
        var input = new ExerciseInputMultipleChoiceTextModel();
        input.addOption(new ExerciseInputMultipleChoiceTextModel.Option("id1", "Option 1"));
        input.addOption(new ExerciseInputMultipleChoiceTextModel.Option("id2", "Option 2"));
        input.addOption(new ExerciseInputMultipleChoiceTextModel.Option("id3", "Option 3"));
        input.addOption(new ExerciseInputMultipleChoiceTextModel.Option("id4", "Option 4"));

        instance.setInput(input);

        return instance;
    }
}
