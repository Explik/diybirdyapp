package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.FlashcardDeckDto;
import com.explik.diybirdyapp.service.FlashcardDeckService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FlashcardDeckController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    FlashcardDeckService service;

    @GetMapping("/flashcard-deck")
    public List<FlashcardDeckDto> getAll() {
        var models = service.getAll();

        return models.stream()
            .map(s -> modelMapper.map(s, FlashcardDeckDto.class))
            .collect(Collectors.toList());
    }
}
