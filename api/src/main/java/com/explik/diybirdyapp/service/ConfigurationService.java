package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.persistence.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationService {
    @Autowired
    ConfigurationRepository repository;

    public ConfigurationDto getById(String configId) {
        return repository.get(configId);
    }

    public ConfigurationDto update(ConfigurationDto configModel) {
        return repository.update(configModel);
    }

    public void deleteById(String configId) {
        repository.delete(configId);
    }
}
