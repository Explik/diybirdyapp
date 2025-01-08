package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.*;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.event.ExerciseAnsweredEvent;
import com.explik.diybirdyapp.model.ExerciseInputModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExerciseController {
    @Autowired
    ExerciseService exerciseService;

    @Autowired
    GenericMapper<ExerciseModel, ExerciseDto> exerciseMapper;

    @Autowired
    GenericMapper<ExerciseInputDto, ExerciseInputModel> exerciseInputMapper;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @GetMapping("/exercise")
    public List<ExerciseDto> get() {
        var models = exerciseService.getExercises();

        return models.stream()
                .map(exerciseMapper::map)
                .toList();
    }

    @GetMapping("/exercise/{id}")
    public ExerciseDto get(@PathVariable String id) {
        var model = exerciseService.getExercise(id);
        return exerciseMapper.map(model);
    }

    @PostMapping("/exercise/{id}/answer")
    public ExerciseDto submitAnswer(@PathVariable String id, @RequestBody ExerciseInputDto dto) {
        var model = exerciseInputMapper.map(dto);
        var newModel = exerciseService.submitExerciseAnswer(id, model);

        var event = new ExerciseAnsweredEvent(
                this,
                newModel.getType(),
                newModel.getId(),
                newModel.getAnswerId());
        eventPublisher.publishEvent(event);

        return exerciseMapper.map(newModel);
    }
}