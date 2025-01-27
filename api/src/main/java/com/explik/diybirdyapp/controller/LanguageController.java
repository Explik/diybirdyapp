package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.service.LanguageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LanguageController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    LanguageService service;

    @GetMapping("/language")
    public List<FlashcardLanguageDto> getAll() {
        var models = service.getAll();

        return models.stream()
            .map(s -> modelMapper.map(s, FlashcardLanguageDto.class))
            .collect(Collectors.toList());
    }
}
