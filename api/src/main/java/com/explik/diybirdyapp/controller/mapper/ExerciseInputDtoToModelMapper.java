package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.ExerciseInputDto;
import com.explik.diybirdyapp.controller.dto.ExerciseInputMultipleChoiceTextDto;
import com.explik.diybirdyapp.controller.dto.ExerciseInputRecognizabilityRatingDto;
import com.explik.diybirdyapp.controller.dto.ExerciseInputTextDto;
import com.explik.diybirdyapp.model.ExerciseInputModel;
import com.explik.diybirdyapp.model.ExerciseInputMultipleChoiceTextModel;
import com.explik.diybirdyapp.model.ExerciseInputRecognizabilityRatingModel;
import com.explik.diybirdyapp.model.ExerciseInputTextModel;
import org.springframework.stereotype.Component;

@Component
public class ExerciseInputDtoToModelMapper extends BaseDtoToModelMapper implements GenericMapper<ExerciseInputDto, ExerciseInputModel> {
    @Override
    public ExerciseInputModel map(ExerciseInputDto source) {
        if (source instanceof ExerciseInputTextDto)
            return modelMapper.map(source, ExerciseInputTextModel.class);
        if (source instanceof ExerciseInputMultipleChoiceTextDto)
            return modelMapper.map(source, ExerciseInputMultipleChoiceTextModel.class);
        if (source instanceof ExerciseInputRecognizabilityRatingDto)
            return modelMapper.map(source, ExerciseInputRecognizabilityRatingModel.class);

        return modelMapper.map(source, ExerciseInputModel.class);
    }
}
