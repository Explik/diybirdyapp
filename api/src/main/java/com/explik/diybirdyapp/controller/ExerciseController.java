package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.model.Exercise;

import com.explik.diybirdyapp.serializer.ExerciseAnswerSerializer;
import com.explik.diybirdyapp.serializer.ExerciseSerializer;
import com.explik.diybirdyapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class ExerciseController {
    @Autowired
    private ExerciseSerializer exerciseSerializer;

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping("/exercise")
    public Exercise create(@RequestBody String json)  {
        Exercise exercise = exerciseSerializer.deserialize(json);
        return exerciseService.createExercise(exercise);
    }

    @GetMapping("/exercise")
    public List<Exercise> get() {
        return exerciseService.getExercises();
    }

    @GetMapping("/exercise/{id}")
    public Exercise get(@PathVariable String id) {
        return exerciseService.getExercise(id);
    }

    @PostMapping("/exercise/{id}/answer")
    public Exercise createExerciseAnswer(@PathVariable String id, @RequestBody String json) {
        Exercise exercise = exerciseSerializer.deserialize(json);
        exercise.setId(id);

        return exerciseService.createExerciseAnswer(exercise);
    }
}