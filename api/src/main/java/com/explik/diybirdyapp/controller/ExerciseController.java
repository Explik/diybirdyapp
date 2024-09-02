package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.*;
import com.explik.diybirdyapp.graph.model.*;
import com.explik.diybirdyapp.service.ExerciseService;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.modelmapper.spi.*;

@RestController
public class ExerciseController {
    private final ModelMapper modelMapper;

    @Autowired
    ExerciseService exerciseService;

    ExerciseController() {
        modelMapper = new ModelMapper();
        modelMapper.addConverter(exerciseContentConverter);
        modelMapper.addConverter(exerciseInputConverter);
    }

//    @PostMapping("/exercise")
//    public Exercise create(@RequestBody String json)  {
//        Exercise exercise = exerciseSerializer.deserialize(json);
//        return exerciseService.createExercise(exercise);
//    }

    @GetMapping("/exercise")
    public List<ExerciseDto> get() {
        var models = exerciseService.getExercises();

        return models.stream()
                .map(s -> modelMapper.map(s, ExerciseDto.class))
                .toList();
    }

    @GetMapping("/exercise/{id}")
    public ExerciseDto get(@PathVariable String id) {
        var model = exerciseService.getExercise(id);

        return modelMapper.map(model, ExerciseDto.class);
    }

//    @PostMapping("/exercise/{id}/answer")
//    public ExerciseFeedback createExerciseAnswer(@PathVariable String id, @RequestBody String json) {
//        Exercise exercise = exerciseSerializer.deserialize(json);
//        exercise.setId(id);
//
//        return exerciseService.createExerciseAnswer(exercise);
//    }

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