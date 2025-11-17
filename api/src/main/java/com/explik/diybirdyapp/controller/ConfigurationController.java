package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.admin.ConfigurationDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.model.admin.ConfigurationModel;
import com.explik.diybirdyapp.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConfigurationController {
    @Autowired
    GenericMapper<ConfigurationDto, ConfigurationModel> incomingMapper;

    @Autowired
    GenericMapper<ConfigurationModel, ConfigurationDto> outgoingMapper;

    @Autowired
    ConfigurationService service;

    @GetMapping("/config/{id}")
    public ConfigurationDto getConfigById(@PathVariable("id") String configId) {
        var model = service.getById(configId);
        return outgoingMapper.map(model);
    }

    @PutMapping("/config/{id}")
    public ConfigurationDto updateConfigById(@PathVariable("id") String configId, @RequestBody ConfigurationDto configDto) {
        var model = incomingMapper.map(configDto);
        model.setId(configId);

        var updatedModel = service.update(model);
        return outgoingMapper.map(updatedModel);
    }

    @DeleteMapping("/config/{id}")
    public void deleteConfigById(@PathVariable("id") String configId) {
        service.deleteById(configId);
    }
}
