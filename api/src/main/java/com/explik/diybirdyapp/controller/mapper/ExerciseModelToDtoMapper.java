package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.*;
import com.explik.diybirdyapp.graph.model.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ExerciseModelToDtoMapper implements GenericMapper<ExerciseModel, ExerciseDto> {
    private final ModelMapper modelMapper;

    public ExerciseModelToDtoMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(exerciseContentConverter);
        modelMapper.addConverter(exerciseInputConverter);
    }

    public ExerciseDto map(ExerciseModel model) {
        return modelMapper.map(model, ExerciseDto.class);
    }

    Converter<ExerciseContentModel, ExerciseContentDto> exerciseContentConverter = new AbstractConverter<ExerciseContentModel, ExerciseContentDto>() {
        protected ExerciseContentDto convert(ExerciseContentModel source) {
            if (source instanceof ExerciseContentTextModel)
                return modelMapper.map(source, ExerciseContentTextDto.class);
            if (source instanceof ExerciseContentFlashcardModel)
                return modelMapper.map(source, ExerciseContentFlashcardDto.class);
            return null;
        }
    };

    Converter<ExerciseInputModel, ExerciseInputDto> exerciseInputConverter = new AbstractConverter<ExerciseInputModel, ExerciseInputDto>() {
        @Override
        protected ExerciseInputDto convert(ExerciseInputModel source) {
            if (source instanceof ExerciseInputTextModel)
                return modelMapper.map(source, ExerciseInputTextDto.class);
            if (source instanceof ExerciseInputMultipleChoiceTextModel)
                return modelMapper.map(source, ExerciseInputMultipleChoiceTextDto.class);
            return null;
        }
    };
}
