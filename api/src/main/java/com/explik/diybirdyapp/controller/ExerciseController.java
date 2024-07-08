package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.repository.ExerciseRepository;
import com.explik.diybirdyapp.model.Exercise;

import com.explik.diybirdyapp.serializer.ExerciseSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExerciseController {
    @Autowired
    private ExerciseSerializer exerciseSerializer;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private static final Logger logger = LoggerFactory.getLogger(ExerciseController.class);

    @GetMapping("/exercise")
    public ResponseEntity<?> retrieveExercises() {
        try {
            var exercises = exerciseRepository.getAll();
            String exercisesJson = exerciseSerializer.serializeList(exercises);
            return new ResponseEntity<>(exercisesJson, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("An error occurred while processing the request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");
        }
    }

    @PostMapping("/exercise")
    public ResponseEntity<?> createExercise(@RequestBody String exerciseJson) {
        try {
            Exercise exercise = exerciseSerializer.deserialize(exerciseJson);
            exerciseRepository.add(exercise);

            Exercise persistedExercise = exerciseRepository.getById(exercise.getId());
            String persistedExerciseJson = exerciseSerializer.serialize(persistedExercise);
            return new ResponseEntity<>(persistedExerciseJson, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("An error occurred while processing the request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");
        }
    }

    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<?> retrieveExercise(@PathVariable String exerciseId) {
        try {
            Exercise exercise = exerciseRepository.getById(exerciseId);
            String exerciseJson = exerciseSerializer.serialize(exercise);
            return new ResponseEntity<>(exerciseJson, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("An error occurred while processing the request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");
        }
    }

    @PostMapping("/exercise/{exerciseId}/answer")
    public void createExerciseAnswer(@PathVariable String exerciseId, @RequestBody String answerJson) {

    }

    @GetMapping("/exercise/{exerciseId}/answer/{answerId}")
    public void retrieveExerciseAnswer(@PathVariable String exerciseId, @PathVariable String answerId) {

    }

    @PostMapping("/exercise/{exerciseId}/answer/{answerId}/feedback")
    public void createExerciseAnswerFeedback(@PathVariable String exerciseId, @PathVariable String answerId, @RequestBody String feedbackJson) {

    }

    @GetMapping("/exercise/{exerciseId}/answer/{answerId}/feedback/{feedbackId}")
    public void retrieveExerciseAnswerFeedback(@PathVariable String exerciseId, @PathVariable String answerId, @PathVariable String feedbackId) {

    }
}