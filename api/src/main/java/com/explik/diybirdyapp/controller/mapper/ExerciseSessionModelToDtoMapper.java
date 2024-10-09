package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.ExerciseDto;
import com.explik.diybirdyapp.controller.dto.ExerciseSessionDto;
import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.model.ExerciseSessionModel;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionModelToDtoMapper extends BaseModelToDtoMapper implements GenericMapper<ExerciseSessionModel, ExerciseSessionDto> {
    @Override
    public ExerciseSessionDto map(ExerciseSessionModel exerciseSessionModel) {
        return modelMapper.map(exerciseSessionModel, ExerciseSessionDto.class);
    }
}
