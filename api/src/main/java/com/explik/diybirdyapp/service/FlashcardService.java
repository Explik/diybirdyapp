package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.FlashcardModel;
import com.explik.diybirdyapp.persistence.repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.util.List;

@Component
public class FlashcardService {
    @Autowired
    FlashcardRepository repository;

    public FlashcardModel add(FlashcardModel model, MultipartFile[] files) {
        // TODO save file
        return repository.add(model);
    }

    public FlashcardModel update(FlashcardModel model, MultipartFile[] files) {
        return repository.update(model);
    }

    public List<FlashcardModel> getAll(@Nullable String setId) {
        return repository.getAll(setId);
    }
}
