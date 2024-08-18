package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.graph.model.FlashcardDeckModel;
import com.explik.diybirdyapp.graph.repository.FlashcardDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlashcardDeckService {
    @Autowired
    FlashcardDeckRepository repository;

    public FlashcardDeckModel add(FlashcardDeckModel model) {
        return repository.add(model);
    }

    public FlashcardDeckModel get(String id) {
        return repository.get(id);
    }

    public List<FlashcardDeckModel> getAll() {
        return repository.getAll();
    }

    public FlashcardDeckModel update(FlashcardDeckModel model) {
        return repository.update(model);
    }
}
