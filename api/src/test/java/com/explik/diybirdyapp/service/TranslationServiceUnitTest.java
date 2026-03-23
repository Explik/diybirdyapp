package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.admin.GoogleTranslationConfigModel;
import com.explik.diybirdyapp.model.admin.TranslationRequestDto;
import com.explik.diybirdyapp.persistence.query.GetGoogleTranslateLanguageCodeQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TranslationServiceUnitTest {
    @Mock
    private Translate translateClient;

    @Mock
    private QueryHandler<GetGoogleTranslateLanguageCodeQuery, GoogleTranslationConfigModel> languageCodeQueryHandler;

    @InjectMocks
    private TranslationService service;

    @Test
    void givenGoogleTranslatedTextWithHtmlEntities_whenTranslateText_thenReturnsUnescapedText() {
        var request = new TranslationRequestDto();
        request.setSourceLanguageId("en");
        request.setTargetLanguageId("da");
        request.setText("I feel less afraid of the idea of \"studying every day\" now.");

        when(languageCodeQueryHandler.handle(any(GetGoogleTranslateLanguageCodeQuery.class)))
                .thenReturn(new GoogleTranslationConfigModel("en", "da"));

        var translation = mock(Translation.class);
        when(translation.getTranslatedText())
                .thenReturn("I feel less afraid of the idea of &quot;studying every day&quot; now.");
        when(translateClient.translate(eq(request.getText()), any(Translate.TranslateOption[].class)))
                .thenReturn(translation);

        var response = service.translateText(request);

        assertEquals("I feel less afraid of the idea of \"studying every day\" now.", response.getTranslatedText());
    }
}