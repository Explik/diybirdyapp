package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.*;
import com.explik.diybirdyapp.model.exercise.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public abstract class BaseDtoToModelMapper {
    protected final ModelMapper modelMapper;

    public BaseDtoToModelMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(exerciseInputConverter);
    }

    Converter<ExerciseInputDto, ExerciseInputModel> exerciseInputConverter = new AbstractConverter<ExerciseInputDto, ExerciseInputModel>() {
        @Override
        protected ExerciseInputModel convert(ExerciseInputDto source) {
            if (source instanceof ExerciseInputTextDto)
                return modelMapper.map(source, ExerciseInputTextModel.class);
            if (source instanceof ExerciseInputMultipleChoiceTextDto)
                return modelMapper.map(source, ExerciseInputMultipleChoiceTextModel.class);
            if (source instanceof ExerciseInputRecognizabilityRatingDto)
                return modelMapper.map(source, ExerciseInputRecognizabilityRatingModel.class);
            if (source instanceof ExerciseInputAudioDto)
                return modelMapper.map(source, ExerciseInputAudioModel.class);
            return null;
        }
    };
}
