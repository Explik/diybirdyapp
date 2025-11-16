package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.admin.ConfigurationDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.model.admin.ConfigurationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConfigurationController {
    @Autowired
    GenericMapper<ConfigurationDto, ConfigurationModel> incomingMapper;

    @Autowired
    GenericMapper<ConfigurationModel, ConfigurationDto> outgoingMapper;

    @GetMapping("/config/{id}")
    public ConfigurationDto getConfigById(@PathVariable("id") String configId) {
        return null;
    }

    @PutMapping("/config/{id}")
    public ConfigurationDto updateConfigById(@PathVariable("id") String configId) {
        return null;
    }

    @DeleteMapping("/config/{id}")
    public void deleteConfigById(@PathVariable("id") String configId) { }
}
