package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.command.*;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VertexBuilderFactory {
    @Autowired
    private CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandHandler;

    @Autowired
    private CommandHandler<CreateTextContentVertexCommand> createTextContentVertexCommandHandler;

    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandCommandHandler;

    @Autowired
    private CommandHandler<CreateFlashcardVertexCommand> createFlashcardVertexCommandHandler;

    @Autowired
    private CommandHandler<CreateFlashcardDeckVertexCommand> createFlashcardDeckVertexCommandHandler;

    @Autowired
    private CommandHandler<CreateLanguageVertexCommand> createLanguageVertexCommandHandler;

    @Autowired
    private CommandHandler<CreateTextToSpeechConfigVertexCommand> createTextToSpeechConfigVertexCommandHandler;

    @Autowired
    private CommandHandler<CreateSpeechToTextConfigVertexCommand> createSpeechToTextConfigVertexCommandHandler;

    @Autowired
    private CommandHandler<CreateTranslateConfigVertexCommand> createTranslateConfigVertexCommandHandler;

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
        factories.createTextContentVertexCommandHandler = createTextContentVertexCommandHandler;
        factories.createFlashcardVertexCommandHandler = createFlashcardVertexCommandHandler;
        factories.createFlashcardDeckVertexCommandHandler = createFlashcardDeckVertexCommandHandler;
        factories.createLanguageVertexCommandHandler = createLanguageVertexCommandHandler;
        factories.createTextToSpeechConfigVertexCommandHandler = createTextToSpeechConfigVertexCommandHandler;
        factories.createSpeechToTextConfigVertexCommandHandler = createSpeechToTextConfigVertexCommandHandler;
        factories.createTranslateConfigVertexCommandHandler = createTranslateConfigVertexCommandHandler;

        builder.injectFactories(factories);

        return builder;
    }
}
