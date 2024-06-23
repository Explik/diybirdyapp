package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.graph.model.Exercise;

import com.explik.diybirdyapp.graph.model.ExerciseFeedback;
import com.explik.diybirdyapp.serializer.ExerciseSerializer;
import com.explik.diybirdyapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ExerciseFeedback createExerciseAnswer(@PathVariable String id, @RequestBody String json) {
        Exercise exercise = exerciseSerializer.deserialize(json);
        exercise.setId(id);

        return exerciseService.createExerciseAnswer(exercise);
    }
}