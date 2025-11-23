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
                .map(this::createOption)
                .toList();
        input.setOptions(inputOptions);

        // Determine option type
        if (!allOptionVertices.isEmpty()) {
            var firstOption = allOptionVertices.getFirst();
            if (firstOption instanceof AudioContentVertex)
                input.setOptionType("audio");
            else if (firstOption instanceof TextContentVertex)
                input.setOptionType("text");
            else if (firstOption instanceof ImageContentVertex)
                input.setOptionType("image");
        }

        return input;
    }

    private ExerciseInputSelectOptionsDto.SelectOptionInputBaseOption createOption(ContentVertex contentVertex) {
        if (contentVertex instanceof AudioContentVertex audioContentVertex)
            return createAudioOption(audioContentVertex);
        if (contentVertex instanceof TextContentVertex textContentVertex)
            return createTextOption(textContentVertex);
        if (contentVertex instanceof ImageContentVertex imageContentVertex)
            return createImageOption(imageContentVertex);

        throw new RuntimeException("Unsupported content type: " + contentVertex.getClass().getName());
    }

    private ExerciseInputSelectOptionsDto.SelectOptionInputAudioOption createAudioOption(AudioContentVertex contentVertex) {
        return new ExerciseInputSelectOptionsDto.SelectOptionInputAudioOption(
                contentVertex.getId(),
                contentVertex.getUrl());
    }

    private ExerciseInputSelectOptionsDto.SelectOptionInputTextOption createTextOption(TextContentVertex textContentVertex) {
        return new ExerciseInputSelectOptionsDto.SelectOptionInputTextOption(
                textContentVertex.getId(),
                textContentVertex.getValue());
    }

    private ExerciseInputSelectOptionsDto.SelectOptionInputImageOption createImageOption(ImageContentVertex imageContentVertex) {
        return new ExerciseInputSelectOptionsDto.SelectOptionInputImageOption(
                imageContentVertex.getId(),
                imageContentVertex.getUrl());
    }
}
