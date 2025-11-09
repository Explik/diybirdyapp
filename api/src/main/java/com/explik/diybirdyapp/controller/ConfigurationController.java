package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.admin.ConfigurationDto;
import com.explik.diybirdyapp.service.ConfigurationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ConfigurationController {
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    ConfigurationService service;

    @GetMapping("/configuration")
    public List<ConfigurationDto> getAll(@RequestParam String languageId) {
        var models = service.getAll(languageId);

        if (languageId == null || languageId.isBlank())
            throw new IllegalArgumentException("No languageId parameter is not supported");

        return models.stream()
            .map(s -> modelMapper.map(s, ConfigurationDto.class))
            .collect(Collectors.toList());
    }
}
