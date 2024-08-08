package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.controller.dto.FlashcardDto;
import com.explik.diybirdyapp.graph.model.FlashcardModel;
import com.explik.diybirdyapp.graph.repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;

@Component
public class FlashcardService {
    @Autowired
    FlashcardRepository repository;

    public FlashcardModel add(FlashcardModel model) {
        return repository.add(model);
    }

    public FlashcardModel update(FlashcardModel model) {
        return repository.update(model);
    }

    public List<FlashcardModel> getAll(@Nullable String setId) {
        return repository.getAll(setId);
    }
}
