package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseInputPairOptionsDto;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.PairVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseInputModelFactoryPairOptions implements ContextualModelFactory<ExerciseVertex, ExerciseInputPairOptionsDto, ExerciseRetrievalContext> {
    @Override
    public ExerciseInputPairOptionsDto create(ExerciseVertex vertex, ExerciseRetrievalContext context) {
        var input = new ExerciseInputPairOptionsDto();
        input.setType(ExerciseInputTypes.PAIR_OPTIONS);

        var optionPairs = vertex
                .getOptionPairs()
                .stream()
                .map(pairVertex -> createOptionPair(vertex, pairVertex))
                .toList();
        var leftOptionsDto = optionPairs.stream().map(OptionPair::leftOption).toList();
        var rightOptionsDto = optionPairs.stream().map(OptionPair::rightOption).toList();
        input.setLeftOptions(leftOptionsDto);
        input.setRightOptions(rightOptionsDto);

        return input;
    }

    private OptionPair createOptionPair(ExerciseVertex vertex, PairVertex pairVertex) {
        var leftSide = pairVertex.getLeftContent();
        var rightSide = pairVertex.getRightContent();

        if (leftSide instanceof TextContentVertex leftTextSide && rightSide instanceof TextContentVertex rightTextSide) {
            var leftOption = new ExerciseInputPairOptionsDto.PairOptionsInputTextOptionDto();
            leftOption.setId(leftTextSide.getId());
            leftOption.setText(leftTextSide.getValue());

            var rightOption = new ExerciseInputPairOptionsDto.PairOptionsInputTextOptionDto();
            rightOption.setId(rightTextSide.getId());
            rightOption.setText(rightTextSide.getValue());

            return new OptionPair(leftOption, rightOption);
        }
        else throw new IllegalArgumentException("Unsupported paired content type: " + leftSide.getClass().getName() + " and " + rightSide.getClass().getName());
    }

    private record OptionPair(ExerciseInputPairOptionsDto.PairOptionsInputOptionDto leftOption, ExerciseInputPairOptionsDto.PairOptionsInputOptionDto rightOption) { }
}
