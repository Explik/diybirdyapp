package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.ExerciseSessionOptionsDto;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsModel;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionOptionsDtoToModelMapper implements GenericMapper<ExerciseSessionOptionsDto, ExerciseSessionOptionsModel> {
    private final ModelMapper modelMapper;

    public ExerciseSessionOptionsDtoToModelMapper() {
        modelMapper = new ModelMapper();
    }

    @Override
    public ExerciseSessionOptionsModel map(ExerciseSessionOptionsDto exerciseSessionOptionsDto) {
        return modelMapper.map(exerciseSessionOptionsDto, ExerciseSessionOptionsModel.class);
    }
}
