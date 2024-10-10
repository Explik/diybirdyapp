package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.persistence.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageService {
    @Autowired
    LanguageRepository repository;

    public List<FlashcardLanguageModel> getAll() {
        return repository.getAll();
    }
}
