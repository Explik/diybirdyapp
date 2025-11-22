package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.dto.exercise.ExerciseSessionOptionsDto;
import com.explik.diybirdyapp.dto.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.service.ExerciseSessionService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExerciseSessionController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    ExerciseSessionService service;

    @PostMapping("/exercise-session")
    public ExerciseSessionDto create(@Valid @RequestBody ExerciseSessionDto dto) {
        return service.add(dto);
    }

    @GetMapping("/exercise-session/{id}")
    public ResponseEntity<ExerciseSessionDto> get(@PathVariable String id) {
        var model = service.get(id);
        if (model == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(model);
    }

    @PostMapping("/exercise-session/{id}/next-exercise")
    public ExerciseSessionDto nextExercise(@PathVariable String id) {
        return service.nextExercise(id);
    }

    @PostMapping("/exercise-session/{id}/skip-exercise")
    public ExerciseSessionDto skipExercise(@PathVariable String id) {
        return service.skipExercise(id);
    }

    @GetMapping("/exercise-session/{id}/options")
    public ExerciseSessionOptionsDto getConfig(@PathVariable String id) {
        return service.getConfig(id);
    }

    @PostMapping("/exercise-session/{id}/apply-options")
    public ExerciseSessionDto updateConfig(@PathVariable String id, @Valid @RequestBody ExerciseSessionOptionsDto dto) {
        return service.updateConfig(id, dto);
    }
}
