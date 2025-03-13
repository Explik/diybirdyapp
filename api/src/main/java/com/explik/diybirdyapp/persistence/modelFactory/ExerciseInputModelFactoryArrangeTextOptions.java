package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseInputArrangeTextOptionsModel;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseInputModelFactoryArrangeTextOptions implements ModelFactory<ExerciseVertex, ExerciseInputArrangeTextOptionsModel> {
    @Override
    public ExerciseInputArrangeTextOptionsModel create(ExerciseVertex vertex) {
        var input = new ExerciseInputArrangeTextOptionsModel();
        input.setType(ExerciseInputTypes.ARRANGE_TEXT_OPTIONS);

        var options = vertex.getOptions();
        options.forEach(v -> input.addOption(createOption(vertex, v)));

        return input;
    }

    private ExerciseInputArrangeTextOptionsModel.Option createOption(ExerciseVertex vertex, ContentVertex contentVertex) {
        if (contentVertex instanceof TextContentVertex textContentVertex) {
            var id = vertex.getId();
            var text = textContentVertex.getValue();

            return new ExerciseInputArrangeTextOptionsModel.Option(id, text);
        }
        throw new RuntimeException("Unsupported content type: " + contentVertex.getClass().getName());
    }
}
