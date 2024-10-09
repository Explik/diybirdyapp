package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.ExerciseDto;
import com.explik.diybirdyapp.controller.dto.ExerciseSessionDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.graph.model.ExerciseModel;
import com.explik.diybirdyapp.graph.model.ExerciseSessionModel;
import com.explik.diybirdyapp.service.ExerciseSessionService;
import jakarta.websocket.server.PathParam;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExerciseSessionController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    ExerciseSessionService service;

    @Autowired
    GenericMapper<ExerciseModel, ExerciseDto> exerciseMapper;

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

    @PostMapping("/exercise-session/{id}/next")
    public ExerciseDto nextExercise(@PathVariable String id) {
        var nextExercise = service.nextExercise(id);
        return exerciseMapper.map(nextExercise);
    }
}
