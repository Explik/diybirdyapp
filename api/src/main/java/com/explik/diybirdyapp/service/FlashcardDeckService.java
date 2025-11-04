package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.content.FlashcardDeckModel;
import com.explik.diybirdyapp.persistence.repository.FlashcardDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlashcardDeckService {
    @Autowired
    FlashcardDeckRepository repository;

    public FlashcardDeckModel add(String userId, FlashcardDeckModel model) {
        return repository.add(userId, model);
    }

    public FlashcardDeckModel get(String userId, String id) {
        return repository.get(userId, id);
    }

    public List<FlashcardDeckModel> getAll(String userId) {
        return repository.getAll(userId);
    }

    public FlashcardDeckModel update(String userId, FlashcardDeckModel model) {
        return repository.update(userId, model);
    }

    public void delete(String userId, String id) {
        repository.delete(userId, id);
    }
}
