package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.content.FlashcardDeckDto;
import com.explik.diybirdyapp.model.content.FlashcardDeckModel;
import com.explik.diybirdyapp.service.FlashcardDeckService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FlashcardDeckController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    FlashcardDeckService service;

    @PostMapping("/flashcard-deck")
    public FlashcardDeckDto create(@Valid @RequestBody FlashcardDeckDto dto) {
        var model = modelMapper.map(dto, FlashcardDeckModel.class);
        var persistedModel = service.add(model);

        return modelMapper.map(persistedModel, FlashcardDeckDto.class);
    }

    @GetMapping("/flashcard-deck/{id}")
    public FlashcardDeckDto get(@PathVariable("id") String id) {
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
    public FlashcardDeckDto update(@Valid @RequestBody FlashcardDeckDto dto) {
        var model = modelMapper.map(dto, FlashcardDeckModel.class);
        var persistedModel = service.update(model);

        return modelMapper.map(persistedModel, FlashcardDeckDto.class);
    }

    @DeleteMapping("flashcard-deck/{id}")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }
}
