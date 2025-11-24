package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.model.content.FlashcardDeckDto;
import com.explik.diybirdyapp.service.FlashcardDeckService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FlashcardDeckController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    FlashcardDeckService service;

    @PostMapping("/flashcard-deck")
    public FlashcardDeckDto create(Authentication authentication, @Valid @RequestBody FlashcardDeckDto dto) {
        var userId = getUserId(authentication);
        var model = modelMapper.map(dto, FlashcardDeckDto.class);

        var persistedModel = service.add(userId, model);

        return modelMapper.map(persistedModel, FlashcardDeckDto.class);
    }

    @GetMapping("/flashcard-deck/{id}")
    public FlashcardDeckDto get(Authentication authentication, @PathVariable("id") String id) {
        var userId = getUserId(authentication);
        var model = service.get(userId, id);

        return modelMapper.map(model, FlashcardDeckDto.class);
    }

    @GetMapping("/flashcard-deck")
    public List<FlashcardDeckDto> getAll(Authentication authentication) {
        var userId = getUserId(authentication);
        var models = service.getAll(userId);

        return models.stream()
            .map(s -> modelMapper.map(s, FlashcardDeckDto.class))
            .collect(Collectors.toList());
    }

    @PutMapping("flashcard-deck")
    public FlashcardDeckDto update(Authentication authentication, @Valid @RequestBody FlashcardDeckDto dto) {
        var userId = getUserId(authentication);
        var model = modelMapper.map(dto, FlashcardDeckDto.class);

        var persistedModel = service.update(userId, model);

        return modelMapper.map(persistedModel, FlashcardDeckDto.class);
    }

    @DeleteMapping("flashcard-deck/{id}")
    public void delete(Authentication authentication, @PathVariable("id") String id) {
        var userId = getUserId(authentication);
        service.delete(userId, id);
    }

    private String getUserId(Authentication authentication) {
        return authentication.getName();
    }
}
