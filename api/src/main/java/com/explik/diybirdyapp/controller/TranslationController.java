package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.model.admin.TranslationRequestDto;
import com.explik.diybirdyapp.model.admin.TranslationResponseDto;
import com.explik.diybirdyapp.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslationController {
    @Autowired
    TranslationService service;

    @PostMapping("/translation/translate")
    public TranslationResponseDto translateText(@RequestBody TranslationRequestDto translationModel) {
        return service.translateText(translationModel);
    }
}
