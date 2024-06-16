package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.model.Exercise;

import com.explik.diybirdyapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

@RestController
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/exercise/{id}")
    public Exercise single(@PathVariable long id) {
        return exerciseService.getExercise(id);
    }
}