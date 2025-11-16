package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.vertexFactory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VertexBuilderFactory {
    @Autowired
    private AudioContentVertexFactory audioContentVertexFactory;

    @Autowired
    private TextContentVertexFactory textContentVertexFactory;

    @Autowired
    private PronunciationVertexFactory pronunciationVertexFactory;

    @Autowired
    private FlashcardVertexFactory flashcardVertexFactory;

    @Autowired
    private FlashcardDeckVertexFactory flashcardDeckVertexFactory;

    @Autowired
    private LanguageVertexFactory languageVertexFactory;

    @Autowired
    private TextToSpeechConfigVertexFactory textToSpeechConfigVertexFactory;

    @Autowired
    private TranslateConfigVertexFactory translationConfigVertexFactory;

    public TextContentVertexBuilder createTextContentVertexBuilder() {
        return injectFactories(new TextContentVertexBuilder());
    }

    public FlashcardVertexBuilder createFlashcardVertexBuilder() {
        return injectFactories(new FlashcardVertexBuilder());
    }

    public FlashcardDeckVertexBuilder createFlashcardDeckVertexBuilder() {
        return injectFactories(new FlashcardDeckVertexBuilder());
    }

    public LanguageVertexBuilder createLanguageVertexBuilder() {
        return injectFactories(new LanguageVertexBuilder());
    }

    private <T extends VertexBuilderBase<?>> T injectFactories(T builder) {
        var factories = new VertexBuilderFactories();
        factories.textContentVertexFactory = textContentVertexFactory;
        factories.audioContentVertexFactory = audioContentVertexFactory;
        factories.pronunciationVertexFactory = pronunciationVertexFactory;
        factories.flashcardVertexFactory = flashcardVertexFactory;
        factories.flashcardDeckVertexFactory = flashcardDeckVertexFactory;
        factories.languageVertexFactory = languageVertexFactory;
        factories.textToSpeechConfigVertexFactory = textToSpeechConfigVertexFactory;
        factories.translationConfigVertexFactory = translationConfigVertexFactory;

        builder.injectFactories(factories);

        return builder;
    }
}
