package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.*;
import com.explik.diybirdyapp.model.exercise.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public abstract class BaseModelToDtoMapper {
    protected final ModelMapper modelMapper;

    public BaseModelToDtoMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(exerciseContentConverter);
        modelMapper.addConverter(exerciseInputConverter);
    }

    Converter<ExerciseContentModel, ExerciseContentDto> exerciseContentConverter = new AbstractConverter<ExerciseContentModel, ExerciseContentDto>() {
        protected ExerciseContentDto convert(ExerciseContentModel source) {
            if (source instanceof ExerciseContentAudioModel)
                return modelMapper.map(source, ExerciseContentAudioDto.class);
            if (source instanceof ExerciseContentTextModel)
                return modelMapper.map(source, ExerciseContentTextDto.class);
            if (source instanceof ExerciseContentImageModel)
                return modelMapper.map(source, ExerciseContentImageDto.class);
            if (source instanceof ExerciseContentVideoModel)
                return modelMapper.map(source, ExerciseContentVideoDto.class);
            if (source instanceof ExerciseContentFlashcardModel)
                return modelMapper.map(source, ExerciseContentFlashcardDto.class);
            if (source instanceof ExerciseContentFlashcardSideModel)
                return modelMapper.map(source, ExerciseContentFlashcardSideDto.class);
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
