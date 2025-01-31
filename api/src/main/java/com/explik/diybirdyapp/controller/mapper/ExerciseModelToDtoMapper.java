package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import org.springframework.stereotype.Component;

@Component
public class ExerciseModelToDtoMapper extends BaseModelToDtoMapper implements GenericMapper<ExerciseModel, ExerciseDto> {
    public ExerciseDto map(ExerciseModel model) {
        return modelMapper.map(model, ExerciseDto.class);
    }
}
