package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.admin.ConfigurationDto;
import com.explik.diybirdyapp.controller.dto.admin.ConfigurationIdentifierDto;
import com.explik.diybirdyapp.controller.dto.content.FlashcardLanguageDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.model.admin.ConfigurationIdentifierModel;
import com.explik.diybirdyapp.model.admin.ConfigurationModel;
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
    GenericMapper<ConfigurationDto, ConfigurationModel> incomingMapper;

    @Autowired
    GenericMapper<ConfigurationModel, ConfigurationDto> outgoingMapper;

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

    @GetMapping("/language/{id}/config")
    public List<ConfigurationDto> getConfigs(
            @PathVariable("id") String languageId,
            @RequestParam(required = false) String type) {
        var configs = service.getLanguageConfigs(languageId, type);

        return configs.stream()
            .map(c -> outgoingMapper.map(c))
            .collect(Collectors.toList());
    }

    @PostMapping("/language/{id}/create-config")
    public ResponseEntity<?> createConfig(
            @PathVariable("id") String languageId,
            @RequestBody ConfigurationDto configDto) {
        var configModel = incomingMapper.map(configDto);
        var createdConfig = service.createLanguageConfig(languageId, configModel);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/language/{id}/attach-config")
    public ResponseEntity<?> attachConfig(
            @PathVariable("id") String languageId,
            @RequestBody ConfigurationIdentifierDto configDto) {
        var modelMapper = new ModelMapper();
        var configModel = modelMapper.map(configDto, ConfigurationIdentifierModel.class);

        service.attachLanguageConfig(languageId, configModel);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/language/{id}/detach-config")
    public ResponseEntity<?> detachConfig(
            @PathVariable("id") String languageId,
            @RequestBody ConfigurationIdentifierDto configDto) {
        var modelMapper = new ModelMapper();
        var configModel = modelMapper.map(configDto, ConfigurationIdentifierModel.class);

        service.detachLanguageConfig(languageId, configModel);

        return ResponseEntity.ok().build();
    }
}
