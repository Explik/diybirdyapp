package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.model.Exercise;

import com.explik.diybirdyapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @PostMapping("/exercise")
    public Exercise create(@RequestBody Map<String, Object> data)  {
        return exerciseService.createExercise(data);
    }

    @GetMapping("/exercise")
    public List<Exercise> get() {
        return exerciseService.getExercises();
    }

    @GetMapping("/exercise/{id}")
    public Exercise get(@PathVariable String id) {
        return exerciseService.getExercise(id);
    }
}