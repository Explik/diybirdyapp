package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseInputModelFactoryMultipleChoice implements ModelFactory<ExerciseVertex, ExerciseInputMultipleChoiceTextModel> {
    @Override
    public ExerciseInputMultipleChoiceTextModel create(ExerciseVertex vertex) {
        var input = new ExerciseInputMultipleChoiceTextModel();
        input.setType("multiple-choice-text-input");

        var correctOptions = vertex.getCorrectOptions();
        correctOptions.forEach(v -> input.addOption(createOption(vertex, v)));

        var incorrectOptions = vertex.getOptions();
        incorrectOptions.forEach(v -> input.addOption(createOption(vertex, v)));

        return input;
    }

    private ExerciseInputMultipleChoiceTextModel.Option createOption(ExerciseVertex vertex, ContentVertex contentVertex) {
        if (contentVertex instanceof TextContentVertex)
            return createTextOption(vertex, (TextContentVertex)contentVertex);
        if (contentVertex instanceof FlashcardVertex)
            return createFlashcardOption(vertex, (FlashcardVertex)contentVertex);

        throw new RuntimeException("Unsupported content type: " + contentVertex.getClass().getName());
    }

    private ExerciseInputMultipleChoiceTextModel.Option createFlashcardOption(ExerciseVertex vertex, FlashcardVertex flashcardVertex) {
        var flashcardSide = vertex.getFlashcardSide();
        var flashcardOption = !flashcardSide.equals("front") ? flashcardVertex.getLeftContent() : flashcardVertex.getRightContent();

        return createTextOption(vertex, (TextContentVertex)flashcardOption);
    }

    private ExerciseInputMultipleChoiceTextModel.Option createTextOption(ExerciseVertex vertex, TextContentVertex textContentVertex) {
        return new ExerciseInputMultipleChoiceTextModel.Option(vertex.getId(), textContentVertex.getValue());
    }
}
