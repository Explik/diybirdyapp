package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.*;
import com.explik.diybirdyapp.model.exercise.*;
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
        if (source instanceof ExerciseInputAudioDto)
            return modelMapper.map(source, ExerciseInputAudioModel.class);

        return modelMapper.map(source, ExerciseInputModel.class);
    }
}
