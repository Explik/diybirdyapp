package com.explik.diybirdyapp.controller.mapper;

import com.explik.diybirdyapp.controller.dto.ExerciseSessionDto;
import com.explik.diybirdyapp.controller.dto.ExerciseSessionProgressDto;
import com.explik.diybirdyapp.model.ExerciseSessionModel;
import com.explik.diybirdyapp.model.ExerciseSessionProgressModel;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionModelToDtoMapper extends BaseModelToDtoMapper implements GenericMapper<ExerciseSessionModel, ExerciseSessionDto> {
    public ExerciseSessionModelToDtoMapper() {
        super();

        modelMapper.addConverter(exerciseSessionProgressConverter);
    }

    @Override
    public ExerciseSessionDto map(ExerciseSessionModel exerciseSessionModel) {
        return modelMapper.map(exerciseSessionModel, ExerciseSessionDto.class);
    }

    Converter<ExerciseSessionProgressModel, ExerciseSessionProgressDto> exerciseSessionProgressConverter = new AbstractConverter<ExerciseSessionProgressModel, ExerciseSessionProgressDto>() {
        @Override
        protected ExerciseSessionProgressDto convert(ExerciseSessionProgressModel source) {
            return modelMapper.map(source, ExerciseSessionProgressDto.class);
        }
    };
}
