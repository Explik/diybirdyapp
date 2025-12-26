package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.admin.GoogleTranslationConfigModel;
import com.explik.diybirdyapp.model.admin.TranslationRequestDto;
import com.explik.diybirdyapp.model.admin.TranslationResponseDto;
import com.explik.diybirdyapp.persistence.query.GetGoogleTranslateLanguageCodeQuery;
import com.explik.diybirdyapp.persistence.query.handler.GetGoogleTranslateLanguageCodeQueryHandler;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TranslationService {
    @Autowired
    private Translate translateClient;
    
    @Autowired
    private QueryHandler<GetGoogleTranslateLanguageCodeQuery, GoogleTranslationConfigModel> languageCodeQueryHandler;
    
    public TranslationResponseDto translateText(TranslationRequestDto model) {
        // Fetch both Google Translate language codes in a single query
        GoogleTranslationConfigModel languageCodes = languageCodeQueryHandler.handle(
                new GetGoogleTranslateLanguageCodeQuery(
                        model.getSourceLanguageId(),
                        model.getTargetLanguageId()
                )
        );
        
        // Use Google Cloud Translate API v2 to translate text
        Translation translation = translateClient.translate(
                model.getText(),
                Translate.TranslateOption.sourceLanguage(languageCodes.getSourceLanguageCode()),
                Translate.TranslateOption.targetLanguage(languageCodes.getTargetLanguageCode())
        );

        // Prepare and return the response DTO
        var dto = new TranslationResponseDto();
        dto.setTranslatedText(translation.getTranslatedText());
        return dto;
    }
}
