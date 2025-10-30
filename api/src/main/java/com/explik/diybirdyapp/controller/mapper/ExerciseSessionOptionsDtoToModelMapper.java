package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.*;
import com.explik.diybirdyapp.model.exercise.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionOptionsDtoToModelMapper implements GenericMapper<ExerciseSessionOptionsDto, ExerciseSessionOptionsModel> {
    private final ModelMapper modelMapper;

    public ExerciseSessionOptionsDtoToModelMapper() {
        modelMapper = new ModelMapper();
    }

    @Override
    public ExerciseSessionOptionsModel map(ExerciseSessionOptionsDto source) {
        if (source instanceof ExerciseSessionOptionsLearnFlashcardsDto)
            return modelMapper.map(source, ExerciseSessionOptionsLearnFlashcardModel.class);
        if (source instanceof ExerciseSessionOptionsReviewFlashcardsDto)
            return modelMapper.map(source, ExerciseSessionOptionsReviewFlashcardsModel.class);
        if (source instanceof ExerciseSessionOptionsSelectFlashcardsDto)
            return modelMapper.map(source, ExerciseSessionOptionsSelectFlashcardsModel.class);
        if (source instanceof ExerciseSessionOptionsWriteFlashcardsDto)
            return modelMapper.map(source, ExerciseSessionOptionsWriteFlashcardsModel.class);

        throw new RuntimeException("Unsupported ExerciseSessionOptionsDto type: " + source.getClass().getName());
    }
}
