package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.ExerciseSessionDto;
import com.explik.diybirdyapp.controller.dto.FlashcardDeckDto;
import com.explik.diybirdyapp.graph.model.ExerciseSessionModel;
import com.explik.diybirdyapp.graph.model.FlashcardDeckModel;
import com.explik.diybirdyapp.graph.vertex.manager.ExerciseSessionFlashcardReviewVertexFactory;
import com.explik.diybirdyapp.service.FlashcardDeckService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class FlashcardDeckController {
    private final ModelMapper modelMapper = new ModelMapper();

    // TODO remove test dependencies
    @Autowired
    private GraphTraversalSource traversalSource;
    @Autowired
    private ExerciseSessionFlashcardReviewVertexFactory exerciseSessionFlashcardReviewVertexFactory;

    @Autowired
    FlashcardDeckService service;

    @PostMapping("/flashcard-deck")
    public FlashcardDeckDto create(@RequestBody FlashcardDeckDto dto) {
        var model = modelMapper.map(dto, FlashcardDeckModel.class);
        var persistedModel = service.add(model);

        return modelMapper.map(persistedModel, FlashcardDeckDto.class);
    }

    @GetMapping("/flashcard-deck/{id}")
    public FlashcardDeckDto get(@PathVariable String id) {
        var model = service.get(id);
        return modelMapper.map(model, FlashcardDeckDto.class);
    }

    @GetMapping("/flashcard-deck")
    public List<FlashcardDeckDto> getAll() {
        var models = service.getAll();

        return models.stream()
            .map(s -> modelMapper.map(s, FlashcardDeckDto.class))
            .collect(Collectors.toList());
    }

    @PutMapping("flashcard-deck")
    public FlashcardDeckDto update(@RequestBody FlashcardDeckDto dto) {
        var model = modelMapper.map(dto, FlashcardDeckModel.class);
        var persistedModel = service.update(model);

        return modelMapper.map(persistedModel, FlashcardDeckDto.class);
    }

    // TODO Remove this test method
    @PostMapping("/flashcard-deck/{id}/review-exercise")
    public ExerciseSessionDto createReviewExercise(@PathVariable String id) {
        var sessionModel = new ExerciseSessionModel();
        sessionModel.setId(UUID.randomUUID().toString());
        sessionModel.setType("flashcard-review");
        sessionModel.setFlashcardDeckId(id);

        exerciseSessionFlashcardReviewVertexFactory.init(
                traversalSource,
                sessionModel);

        return modelMapper.map(sessionModel, ExerciseSessionDto.class);
    }
}
