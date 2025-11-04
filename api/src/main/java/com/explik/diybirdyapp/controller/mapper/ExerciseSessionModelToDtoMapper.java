package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.exercise.ExerciseDto;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseSessionOptionsDto;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseSessionProgressDto;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsModel;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionProgressModel;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionModelToDtoMapper implements GenericMapper<ExerciseSessionModel, ExerciseSessionDto> {
    private final ModelMapper modelMapper;

    @Autowired
    GenericMapper<ExerciseModel, ExerciseDto> exerciseMapper;

    public ExerciseSessionModelToDtoMapper() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(exerciseConverter);
    }

    @Override
    public ExerciseSessionDto map(ExerciseSessionModel exerciseSessionModel) {
        return modelMapper.map(exerciseSessionModel, ExerciseSessionDto.class);
    }

    Converter<ExerciseModel, ExerciseDto> exerciseConverter = new AbstractConverter<ExerciseModel, ExerciseDto>() {
        @Override
        protected ExerciseDto convert(ExerciseModel exerciseModel) {
            if (exerciseModel == null)
                return null;

            return exerciseMapper.map(exerciseModel);
        }
    };
}
