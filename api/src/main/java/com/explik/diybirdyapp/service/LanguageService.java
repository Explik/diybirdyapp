package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.graph.model.LanguageModel;
import com.explik.diybirdyapp.graph.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageService {
    @Autowired
    LanguageRepository repository;

    public List<LanguageModel> getAll() {
        return repository.getAll();
    }
}
