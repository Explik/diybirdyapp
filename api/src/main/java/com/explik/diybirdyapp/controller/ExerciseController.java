package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.exercise.ExerciseDto;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseInputDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.event.ExerciseAnsweredEvent;
import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.service.ExerciseService;
import com.explik.diybirdyapp.service.ExerciseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExerciseController {
    @Autowired
    ExerciseService exerciseService;

    @Autowired
    ExerciseTypeService exerciseTypeService;

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

    @GetMapping("/exercise/types")
    public List<String> getTypes() {
        return exerciseTypeService.getAll();
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