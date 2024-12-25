package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.ExerciseDto;
import com.explik.diybirdyapp.controller.dto.ExerciseSessionDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.model.ExerciseSessionModel;
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
}
