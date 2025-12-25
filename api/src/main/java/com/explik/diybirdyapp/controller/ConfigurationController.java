package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.model.admin.ConfigurationOptionsDto;
import com.explik.diybirdyapp.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConfigurationController {
    @Autowired
    ConfigurationService service;

    @GetMapping("/config/{id}")
    public ResponseEntity<ConfigurationDto> getConfigById(@PathVariable("id") String configId) {
        var model = service.getById(configId);
        if (model == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(model);
    }

    @PutMapping("/config/{id}")
    public ConfigurationDto updateConfigById(@PathVariable("id") String configId, @RequestBody ConfigurationDto configDto) {
        configDto.setId(configId);
        return service.update(configDto);
    }

    @DeleteMapping("/config/{id}")
    public void deleteConfigById(@PathVariable("id") String configId) {
        service.deleteById(configId);
    }

    @PostMapping("/config/available-options")
    public ResponseEntity<ConfigurationOptionsDto> getAvailableOptions(@RequestBody ConfigurationOptionsDto configOptionsDto) {
        var options = service.getAvailableOptions(configOptionsDto);
        if (options == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(options);
    }
}
