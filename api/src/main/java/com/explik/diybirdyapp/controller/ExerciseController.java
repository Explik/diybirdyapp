package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.*;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.model.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.ExerciseModel;
import com.explik.diybirdyapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExerciseController {
    @Autowired
    ExerciseService exerciseService;

    @Autowired
    GenericMapper<ExerciseModel, ExerciseDto> exerciseMapper;

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
    public ExerciseFeedbackModel submitAnswer(@PathVariable String id, @RequestBody ExerciseAnswerModel model) {
        return exerciseService.submitExerciseAnswer(id, model);
    }
}