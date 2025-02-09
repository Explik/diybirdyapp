package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.stereotype.Component;

@Component(ExerciseTypes.MULTIPLE_CHOICE_TEXT + ComponentTypes.MODEL_FACTORY)
public class ExerciseModelFactoryMultipleChoiceText implements ExerciseModelFactory {
    @Override
    public ExerciseModel create(ExerciseVertex vertex) {
        if (vertex == null)
            throw new RuntimeException("Vertex is null");
        if (!vertex.getType().equals(ExerciseTypes.MULTIPLE_CHOICE_TEXT))
            throw new RuntimeException("Vertex type is not MULTIPLE_CHOICE_TEXT");

        var instance = new ExerciseModel();
        instance.setId(vertex.getId());
        instance.setType(vertex.getType());

        var input = new ExerciseInputMultipleChoiceTextModel();
        input.setType("multiple-choice-text-input");
        vertex.getTextContentOptions()
                .forEach(v -> {
                    input.addOption(
                            new ExerciseInputMultipleChoiceTextModel.Option(v.getId(), v.getValue()));
                });
        instance.setInput(input);

        return instance;
    }
}
