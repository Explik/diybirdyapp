package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.graph.model.Exercise;
import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.explik.diybirdyapp.graph.repository.FlashcardRepository;
import com.explik.diybirdyapp.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FlashcardController {
    @Autowired
    FlashcardService service;

    @PostMapping("/flashcard")
    public void create(@RequestBody FlashcardModel model)  {
        service.add(model);
    }

    @GetMapping("/flashcard")
    public List<FlashcardModel> getAll() {
        return service.getAll();
    }
}
