package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateFlashcardDeckVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateLanguageVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertexFactory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VertexBuilderFactory {
    @Autowired
    private CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandHandler;

    @Autowired
    private TextContentVertexFactory textContentVertexFactory;

    @Autowired
    private PronunciationVertexFactory pronunciationVertexFactory;

    @Autowired
    private FlashcardVertexFactory flashcardVertexFactory;

    @Autowired
    private CommandHandler<CreateFlashcardDeckVertexCommand> createFlashcardDeckVertexCommandHandler;

    @Autowired
    private CommandHandler<CreateLanguageVertexCommand> createLanguageVertexCommandHandler;

    @Autowired
    private TextToSpeechConfigVertexFactory textToSpeechConfigVertexFactory;

    @Autowired
    private SpeechToTextConfigVertexFactory speechToTextConfigVertexFactory;

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
        factories.pronunciationVertexFactory = pronunciationVertexFactory;
        factories.flashcardVertexFactory = flashcardVertexFactory;
        factories.createFlashcardDeckVertexCommandHandler = createFlashcardDeckVertexCommandHandler;
        factories.createLanguageVertexCommandHandler = createLanguageVertexCommandHandler;
        factories.textToSpeechConfigVertexFactory = textToSpeechConfigVertexFactory;
        factories.speechToTextConfigVertexFactory = speechToTextConfigVertexFactory;
        factories.translationConfigVertexFactory = translationConfigVertexFactory;

        builder.injectFactories(factories);

        return builder;
    }
}
