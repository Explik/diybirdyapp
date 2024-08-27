package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.FlashcardDto;
import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.explik.diybirdyapp.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.*;

import java.util.*;
import java.util.stream.*;

@RestController
public class FlashcardController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    FlashcardService service;

    @PostMapping("/flashcard")
    public FlashcardDto create(@RequestBody FlashcardDto dto)  {
        var model = modelMapper.map(dto, FlashcardModel.class);
        var newModel = service.add(model);

        return modelMapper.map(newModel, FlashcardDto.class);
    }

    @PutMapping("/flashcard")
    public FlashcardDto update(@RequestBody FlashcardDto dto)  {
        var model = modelMapper.map(dto, FlashcardModel.class);
        var newModel = service.update(model);

        return modelMapper.map(newModel, FlashcardDto.class);
    }

    @GetMapping("/flashcard")
    public List<FlashcardDto> getAll(@RequestParam(required = false) String deckId) {
        var models = service.getAll(deckId);

        return models.stream()
            .map(s -> modelMapper.map(s, FlashcardDto.class))
            .collect(Collectors.toList());
    }
}
