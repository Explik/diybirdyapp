package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.*;
import com.explik.diybirdyapp.model.exercise.*;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionOptionsModelToDtoMapper implements GenericMapper<ExerciseSessionOptionsModel, ExerciseSessionOptionsDto> {
    private final org.modelmapper.ModelMapper modelMapper;

    public ExerciseSessionOptionsModelToDtoMapper() {
        modelMapper = new org.modelmapper.ModelMapper();
    }

    @Override
    public ExerciseSessionOptionsDto map(ExerciseSessionOptionsModel  source) {
        if (source instanceof ExerciseSessionOptionsLearnFlashcardModel)
            return modelMapper.map(source, ExerciseSessionOptionsLearnFlashcardsDto.class);
        if (source instanceof ExerciseSessionOptionsReviewFlashcardsModel)
            return modelMapper.map(source, ExerciseSessionOptionsReviewFlashcardsDto.class);
        if (source instanceof ExerciseSessionOptionsSelectFlashcardsModel)
            return modelMapper.map(source, ExerciseSessionOptionsSelectFlashcardsDto.class);
        if (source instanceof ExerciseSessionOptionsWriteFlashcardsModel)
            return modelMapper.map(source, ExerciseSessionOptionsWriteFlashcardsDto.class);

        throw new RuntimeException("Unsupported ExerciseSessionOptionsModel type: " + source.getClass().getName());
    }
}
