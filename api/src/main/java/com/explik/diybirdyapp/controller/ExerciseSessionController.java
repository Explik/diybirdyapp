package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.exercise.ExerciseSessionOptionsDto;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionOptionsModel;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.service.ExerciseSessionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExerciseSessionController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    ExerciseSessionService service;

    @Autowired
    GenericMapper<ExerciseSessionModel, ExerciseSessionDto> exerciseSessionMapper;

    @Autowired
    GenericMapper<ExerciseSessionOptionsDto, ExerciseSessionOptionsModel> exerciseOptionsMapper;

    @PostMapping("/exercise-session")
    public ExerciseSessionDto create(@RequestBody ExerciseSessionDto dto) {
        var model = modelMapper.map(dto, ExerciseSessionModel.class);
        var newModel = service.add(model);

        return exerciseSessionMapper.map(newModel);
    }

    @GetMapping("/exercise-session/{id}")
    public ExerciseSessionDto get(@PathVariable String id) {
        var model = service.get(id);
        return exerciseSessionMapper.map(model);
    }

    @PostMapping("/exercise-session/{id}/next-exercise")
    public ExerciseSessionDto nextExercise(@PathVariable String id) {
        var nextExercise = service.nextExercise(id);
        return exerciseSessionMapper.map(nextExercise);
    }

    @PostMapping("/exercise-session/{id}/skip-exercise")
    public ExerciseSessionDto skipExercise(@PathVariable String id) {
        var skippedExercise = service.skipExercise(id);
        return exerciseSessionMapper.map(skippedExercise);
    }

    @PostMapping("/exercise-session/{id}/update-config")
    public ExerciseSessionDto updateConfig(@PathVariable String id, @RequestBody ExerciseSessionOptionsDto dto) {
        var model = exerciseOptionsMapper.map(dto);
        var updatedModel = service.updateConfig(id, model);

        return exerciseSessionMapper.map(updatedModel);
    }
}
