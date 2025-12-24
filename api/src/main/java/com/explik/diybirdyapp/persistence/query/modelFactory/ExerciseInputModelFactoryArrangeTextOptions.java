package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseInputArrangeTextOptionsDto;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseInputModelFactoryArrangeTextOptions implements ContextualModelFactory<ExerciseVertex, ExerciseInputArrangeTextOptionsDto, ExerciseRetrievalContext> {
    @Override
    public ExerciseInputArrangeTextOptionsDto create(ExerciseVertex vertex, ExerciseRetrievalContext context) {
        var input = new ExerciseInputArrangeTextOptionsDto();
        input.setType(ExerciseInputTypes.ARRANGE_TEXT_OPTIONS);

        var options = vertex.getOptions().stream().map(v -> createOption(vertex, v)).toArray(ExerciseInputArrangeTextOptionsDto.ArrangeTextOption[]::new);
        input.setOptions(options);

        return input;
    }

    private ExerciseInputArrangeTextOptionsDto.ArrangeTextOption createOption(ExerciseVertex vertex, ContentVertex contentVertex) {
        if (contentVertex instanceof TextContentVertex textContentVertex) {
            var id = vertex.getId();
            var text = textContentVertex.getValue();

            return new ExerciseInputArrangeTextOptionsDto.ArrangeTextOption(id, text);
        }
        throw new RuntimeException("Unsupported content type: " + contentVertex.getClass().getName());
    }
}
