package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.content.FlashcardDeckDto;
import com.explik.diybirdyapp.persistence.repository.FlashcardDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlashcardDeckService {
    @Autowired
    FlashcardDeckRepository repository;

    public FlashcardDeckDto add(String userId, FlashcardDeckDto model) {
        return repository.add(userId, model);
    }

    public FlashcardDeckDto get(String userId, String id) {
        return repository.get(userId, id);
    }

    public List<FlashcardDeckDto> getAll(String userId) {
        return repository.getAll(userId);
    }

    public FlashcardDeckDto update(String userId, FlashcardDeckDto model) {
        return repository.update(userId, model);
    }

    public void delete(String userId, String id) {
        repository.delete(userId, id);
    }
}
