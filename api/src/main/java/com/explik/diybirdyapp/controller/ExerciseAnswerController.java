package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ExerciseAnswerController {
    @Autowired
    ExerciseService exerciseService;

    @PostMapping("/exercise-answer/{id}/feedback")
    public ResponseEntity<Void> submitFeedback(
            @PathVariable String id,
            @RequestBody Map<String, String> feedbackRequest) {
        
        String feedbackType = feedbackRequest.get("type");
        
        if (feedbackType == null || feedbackType.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        exerciseService.submitExerciseAnswerFeedback(id, feedbackType);
        
        return ResponseEntity.ok().build();
    }
}
