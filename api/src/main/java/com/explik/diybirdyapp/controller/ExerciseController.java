package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.*;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
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
    @Autowired
    ExerciseService exerciseService;

    @Autowired
    GenericMapper<ExerciseModel, ExerciseDto> exerciseMapper;

//    @PostMapping("/exercise")
//    public Exercise create(@RequestBody String json)  {
//        Exercise exercise = exerciseSerializer.deserialize(json);
//        return exerciseService.createExercise(exercise);
//    }

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

//    @PostMapping("/exercise/{id}/answer")
//    public ExerciseFeedback createExerciseAnswer(@PathVariable String id, @RequestBody String json) {
//        Exercise exercise = exerciseSerializer.deserialize(json);
//        exercise.setId(id);
//
//        return exerciseService.createExerciseAnswer(exercise);
//    }
}