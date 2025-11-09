package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.admin.ConfigurationModel;
import com.explik.diybirdyapp.persistence.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigurationService {
    @Autowired
    ConfigurationRepository repository;

    public List<ConfigurationModel> getAll(String languageId) {
        return repository.getAll(languageId);
    }
}
