package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.*;
import com.explik.diybirdyapp.model.exercise.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ExerciseInputDtoToModelMapper implements GenericMapper<ExerciseInputDto, ExerciseInputModel> {
    private final ModelMapper modelMapper;

    public ExerciseInputDtoToModelMapper() {
        modelMapper = new ModelMapper();
    }

    @Override
    public ExerciseInputModel map(ExerciseInputDto source) {
        if (source instanceof ExerciseInputArrangeTextOptionsDto)
            return modelMapper.map(source, ExerciseInputArrangeTextOptionsModel.class);
        if (source instanceof ExerciseInputWriteTextDto)
            return modelMapper.map(source, ExerciseInputTextModel.class);
        if (source instanceof ExerciseInputSelectOptionsDto)
            return modelMapper.map(source, ExerciseInputSelectOptionsModel.class);
        if (source instanceof ExerciseInputSelectReviewOptionsDto)
            return modelMapper.map(source, ExerciseInputRecognizabilityRatingModel.class);
        if (source instanceof ExerciseInputPairOptionsDto)
            return modelMapper.map(source, ExerciseInputPairOptionsModel.class);
        if (source instanceof ExerciseInputRecordAudioDto)
            return modelMapper.map(source, ExerciseInputAudioModel.class);

        return modelMapper.map(source, ExerciseInputModel.class);
    }
}
