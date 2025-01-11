package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.persistence.repository.FlashcardLanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageService {
    @Autowired
    FlashcardLanguageRepository repository;

    public List<FlashcardLanguageModel> getAll() {
        return repository.getAll();
    }
}
