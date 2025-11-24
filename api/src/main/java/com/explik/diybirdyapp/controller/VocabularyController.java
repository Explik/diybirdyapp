package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.model.content.VocabularyDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VocabularyController {
    @Autowired
    VocabularyService service;

    @GetMapping("/vocabulary")
    public VocabularyDto get() {
        return service.get();
    }
}
