package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseInputPairOptionsModel;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.PairVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseInputModelFactoryPairOptions implements ContextualModelFactory<ExerciseVertex, ExerciseInputPairOptionsModel, ExerciseRetrievalContext> {
    @Override
    public ExerciseInputPairOptionsModel create(ExerciseVertex vertex, ExerciseRetrievalContext context) {
        var input = new ExerciseInputPairOptionsModel();
        input.setType(ExerciseInputTypes.PAIR_OPTIONS);

        var optionPairs = vertex.getOptionPairs();
        optionPairs.forEach(v -> input.addOptionPair(createOptionPair(vertex, v)));

        return input;
    }

    private ExerciseInputPairOptionsModel.OptionPair createOptionPair(ExerciseVertex vertex, PairVertex pairVertex) {
        var leftSide = pairVertex.getLeftContent();
        var rightSide = pairVertex.getRightContent();

        if (leftSide instanceof TextContentVertex leftTextSide && rightSide instanceof TextContentVertex rightTextSide) {
            var leftOption = new ExerciseInputPairOptionsModel.Option(leftTextSide.getId(), leftTextSide.getValue());
            var rightOption = new ExerciseInputPairOptionsModel.Option(rightTextSide.getId(), rightTextSide.getValue());

            return new ExerciseInputPairOptionsModel.OptionPair(leftOption, rightOption);
        }
        else throw new IllegalArgumentException("Unsupported paired content type: " + leftSide.getClass().getName() + " and " + rightSide.getClass().getName());
    }
}
