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
        var flashcardVertex = vertex.getFlashcardContent();
        var isFront = vertex.getFlashcardSide().equals("front");
        instance.setContent(flashcardVertex.toExerciseContentFlashcardModel(isFront, !isFront));

        // Add input
        var inputModel = new ExerciseInputMultipleChoiceTextModel();

        var correctOption = !vertex.getFlashcardSide().equals("front") ? flashcardVertex.getLeftContent() : flashcardVertex.getRightContent();
        inputModel.addOption(new ExerciseInputMultipleChoiceTextModel.Option(
                flashcardVertex.getId(),
                correctOption.getValue()));

        for(var incorrectOption : vertex.getFlashcardOptions()) {
            var optionSideVertex = !vertex.getFlashcardSide().equals("front") ? incorrectOption.getLeftContent() : incorrectOption.getRightContent();

            inputModel.addOption(
                    new ExerciseInputMultipleChoiceTextModel.Option(
                            incorrectOption.getId(),
                            optionSideVertex.getValue()));
        }
        instance.setInput(inputModel);

        return instance;
    }
}
