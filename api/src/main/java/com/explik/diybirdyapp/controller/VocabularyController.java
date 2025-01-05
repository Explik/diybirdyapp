package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.controller.dto.VocabularyDto;
import com.explik.diybirdyapp.controller.mapper.GenericMapper;
import com.explik.diybirdyapp.model.VocabularyModel;
import com.explik.diybirdyapp.service.VocabularyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class VocabularyController {
    @Autowired
    GenericMapper<VocabularyModel, VocabularyDto> vocabularyMapper;

    @Autowired
    VocabularyService service;

    @GetMapping("/vocabulary")
    public VocabularyDto get() {
        var model = service.get();
        return vocabularyMapper.map(model);
    }
}
