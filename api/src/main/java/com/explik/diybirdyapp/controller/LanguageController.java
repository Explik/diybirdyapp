package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.model.admin.ConfigurationIdentifierDto;
import com.explik.diybirdyapp.model.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.service.LanguageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LanguageController {
    @Autowired
    LanguageService service;

    // TODO replace with JSON in front-end
    @GetMapping("/language")
    public List<FlashcardLanguageDto> getAll() {
        var models = service.getAll();

        var mapper = new ModelMapper();

        return models.stream()
            .map(s -> mapper.map(s, FlashcardLanguageDto.class))
            .collect(Collectors.toList());
    }

    @PutMapping("/language/{id}")
    public ResponseEntity<?> updateLanguage(
            @PathVariable("id") String languageId,
            @RequestBody FlashcardLanguageDto languageDto) {
        var mapper = new ModelMapper();
        var languageModel = mapper.map(languageDto, FlashcardLanguageDto.class);
        languageModel.setId(languageId);
        service.update(languageModel);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/language")
    public ResponseEntity<?> createLanguage(
            @RequestBody FlashcardLanguageDto languageDto) {
        var mapper = new ModelMapper();
        var languageModel = mapper.map(languageDto, FlashcardLanguageDto.class);
        service.create(languageModel);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/language/{id}/config")
    public List<ConfigurationDto> getConfigs(
            @PathVariable("id") String languageId,
            @RequestParam(required = false) String type) {

        return service.getLanguageConfigs(languageId, type);
    }

    @PostMapping("/language/{id}/create-config")
    public ResponseEntity<?> createConfig(
            @PathVariable("id") String languageId,
            @RequestBody ConfigurationDto configDto) {

        var createdConfig = service.createLanguageConfig(languageId, configDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/language/{id}/attach-config")
    public ResponseEntity<?> attachConfig(
            @PathVariable("id") String languageId,
            @RequestBody ConfigurationIdentifierDto configDto) {
        service.attachLanguageConfig(languageId, configDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/language/{id}/detach-config")
    public ResponseEntity<?> detachConfig(
            @PathVariable("id") String languageId,
            @RequestBody ConfigurationIdentifierDto configDto) {

        service.detachLanguageConfig(languageId, configDto);

        return ResponseEntity.ok().build();
    }
}
