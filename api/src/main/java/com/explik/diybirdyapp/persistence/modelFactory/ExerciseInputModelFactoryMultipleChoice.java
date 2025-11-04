package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseInputSelectOptionsModel;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

@Component
public class ExerciseInputModelFactoryMultipleChoice implements ContextualModelFactory<ExerciseVertex, ExerciseInputSelectOptionsModel, ExerciseRetrievalContext> {
    @Override
    public ExerciseInputSelectOptionsModel create(ExerciseVertex vertex, ExerciseRetrievalContext context) {
        var input = new ExerciseInputSelectOptionsModel();
        input.setType(ExerciseInputTypes.SELECT_OPTIONS);

        var correctOptions = vertex.getCorrectOptions();
        correctOptions.forEach(v -> input.addOption(createOption(vertex, v)));

        var incorrectOptions = vertex.getOptions();
        incorrectOptions.forEach(v -> input.addOption(createOption(vertex, v)));

        return input;
    }

    private ExerciseInputSelectOptionsModel.BaseOption createOption(ExerciseVertex vertex, ContentVertex contentVertex) {
        if (contentVertex instanceof AudioContentVertex audioContentVertex)
            return createAudioOption(vertex, audioContentVertex);
        if (contentVertex instanceof TextContentVertex textContentVertex)
            return createTextOption(vertex, textContentVertex);
        if (contentVertex instanceof ImageContentVertex imageContentVertex)
            return createImageOption(vertex, imageContentVertex);

        throw new RuntimeException("Unsupported content type: " + contentVertex.getClass().getName());
    }

    private ExerciseInputSelectOptionsModel.AudioOption createAudioOption(ExerciseVertex vertex, AudioContentVertex contentVertex) {
        return new ExerciseInputSelectOptionsModel.AudioOption(
                vertex.getId(),
                contentVertex.getUrl());
    }

    private ExerciseInputSelectOptionsModel.TextOption createTextOption(ExerciseVertex vertex, TextContentVertex textContentVertex) {
        return new ExerciseInputSelectOptionsModel.TextOption(
                vertex.getId(),
                textContentVertex.getValue());
    }

    private ExerciseInputSelectOptionsModel.ImageOption createImageOption(ExerciseVertex vertex, ImageContentVertex imageContentVertex) {
        return new ExerciseInputSelectOptionsModel.ImageOption(
                vertex.getId(),
                imageContentVertex.getUrl());
    }
}
