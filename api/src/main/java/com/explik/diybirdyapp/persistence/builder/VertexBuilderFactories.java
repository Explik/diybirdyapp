package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.command.CreateFlashcardDeckVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertexFactory.*;

public class VertexBuilderFactories {
    // Declarations in alphabetical order
    public CommandHandler<CreateFlashcardDeckVertexCommand> createFlashcardDeckVertexCommandHandler;
    public FlashcardVertexFactory flashcardVertexFactory;
    public LanguageVertexFactory languageVertexFactory;
    public PronunciationVertexFactory pronunciationVertexFactory;
    public TextContentVertexFactory textContentVertexFactory;
    public TextToSpeechConfigVertexFactory textToSpeechConfigVertexFactory;
    public SpeechToTextConfigVertexFactory speechToTextConfigVertexFactory;
    public TranslateConfigVertexFactory translationConfigVertexFactory;
}
