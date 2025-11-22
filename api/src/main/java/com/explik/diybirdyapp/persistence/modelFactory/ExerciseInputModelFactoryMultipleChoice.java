package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.dto.exercise.ExerciseInputSelectOptionsDto;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ExerciseInputModelFactoryMultipleChoice implements ContextualModelFactory<ExerciseVertex, ExerciseInputSelectOptionsDto, ExerciseRetrievalContext> {
    @Override
    public ExerciseInputSelectOptionsDto create(ExerciseVertex vertex, ExerciseRetrievalContext context) {
        var allOptionVertices = new ArrayList<ContentVertex>();
        allOptionVertices.addAll(vertex.getCorrectOptions());
        allOptionVertices.addAll(vertex.getOptions());

        var input = new ExerciseInputSelectOptionsDto();
        input.setType(ExerciseInputTypes.SELECT_OPTIONS);

        var inputOptions = allOptionVertices
                .stream()
                .map(v -> createOption(vertex, v))
                .toList();
        input.setOptions(inputOptions);

        return input;
    }

    private ExerciseInputSelectOptionsDto.SelectOptionInputBaseOption createOption(ExerciseVertex vertex, ContentVertex contentVertex) {
        if (contentVertex instanceof AudioContentVertex audioContentVertex)
            return createAudioOption(vertex, audioContentVertex);
        if (contentVertex instanceof TextContentVertex textContentVertex)
            return createTextOption(vertex, textContentVertex);
        if (contentVertex instanceof ImageContentVertex imageContentVertex)
            return createImageOption(vertex, imageContentVertex);

        throw new RuntimeException("Unsupported content type: " + contentVertex.getClass().getName());
    }

    private ExerciseInputSelectOptionsDto.SelectOptionInputAudioOption createAudioOption(ExerciseVertex vertex, AudioContentVertex contentVertex) {
        return new ExerciseInputSelectOptionsDto.SelectOptionInputAudioOption(
                vertex.getId(),
                contentVertex.getUrl());
    }

    private ExerciseInputSelectOptionsDto.SelectOptionInputTextOption createTextOption(ExerciseVertex vertex, TextContentVertex textContentVertex) {
        return new ExerciseInputSelectOptionsDto.SelectOptionInputTextOption(
                vertex.getId(),
                textContentVertex.getValue());
    }

    private ExerciseInputSelectOptionsDto.SelectOptionInputImageOption createImageOption(ExerciseVertex vertex, ImageContentVertex imageContentVertex) {
        return new ExerciseInputSelectOptionsDto.SelectOptionInputImageOption(
                vertex.getId(),
                imageContentVertex.getUrl());
    }
}
