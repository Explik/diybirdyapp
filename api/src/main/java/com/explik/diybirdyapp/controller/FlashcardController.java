package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.FlashcardDto;
import com.explik.diybirdyapp.event.FlashcardAddedEvent;
import com.explik.diybirdyapp.event.FlashcardUpdatedEvent;
import com.explik.diybirdyapp.model.FlashcardModel;
import com.explik.diybirdyapp.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.*;

@RestController
public class FlashcardController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    FlashcardService service;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostMapping("/flashcard")
    public FlashcardDto create(@RequestBody FlashcardDto dto) {
        var model = modelMapper.map(dto, FlashcardModel.class);
        var newModel = service.add(model, null);

        eventPublisher.publishEvent(new FlashcardAddedEvent(this, newModel.getId()));

        return modelMapper.map(newModel, FlashcardDto.class);
    }

    @PostMapping("/flashcard/rich")
    public FlashcardDto createRich(
            @RequestPart("flashcard") FlashcardDto dto,
            @RequestPart(value = "files", required = false)MultipartFile[] file) {
        var model = modelMapper.map(dto, FlashcardModel.class);
        var newModel = service.add(model, file);

        eventPublisher.publishEvent(new FlashcardAddedEvent(this, newModel.getId()));

        return modelMapper.map(newModel, FlashcardDto.class);
    }

    @PutMapping("/flashcard")
    public FlashcardDto update(@RequestBody FlashcardDto dto) {
        var model = modelMapper.map(dto, FlashcardModel.class);
        var newModel = service.update(model, null);

        eventPublisher.publishEvent(new FlashcardUpdatedEvent(this, newModel.getId()));

        return modelMapper.map(newModel, FlashcardDto.class);
    }

    @PutMapping("/flashcard/rich")
    public FlashcardDto update(
            @RequestPart("flashcard") FlashcardDto dto,
            @RequestPart(value = "files", required = false)MultipartFile[] files) {
        var model = modelMapper.map(dto, FlashcardModel.class);
        var newModel = service.update(model, files);

        eventPublisher.publishEvent(new FlashcardUpdatedEvent(this, newModel.getId()));

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
