package com.explik.diybirdyapp.manager.contentCreationManager;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.AudioContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.service.TextToSpeechService;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import com.explik.diybirdyapp.persistence.query.GetVoiceByLanguageIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.model.internal.VoiceModel;
import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import com.explik.diybirdyapp.persistence.vertex.PronunciationVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Text-to-Speech Content Creation Manager - Dispatches async TTS generation tasks.
 * 
 * This manager checks if a text content vertex has a language with TTS configuration,
 * and if so, dispatches an async task to generate pronunciation audio and save it
 * as a PronunciationVertex connected to the TextContentVertex.
 * 
 * The process happens asynchronously and should not be awaited by the exercise session manager.
 */
@Component
public class TextToSpeechContentCreationManager {
    
    @Autowired
    private TextToSpeechService textToSpeechService;
    
    @Autowired
    private BinaryStorageService binaryStorageService;
    
    @Autowired
    private QueryHandler<GetVoiceByLanguageIdQuery, VoiceModel> getVoiceByLanguageIdQueryHandler;
    
    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandHandler;
    
    /**
     * Dispatches async TTS generation for a text content vertex.
     * 
     * This method checks if the text content has a language with TTS configuration,
     * and if so, generates pronunciation audio asynchronously.
     * 
     * @param textContentVertex The text content vertex to generate pronunciation for
     * @param onSuccessCallback Optional callback to invoke with the created PronunciationVertex after successful generation
     */
    @Async
    public void dispatchTextToSpeechGeneration(TextContentVertex textContentVertex, Consumer<PronunciationVertex> onSuccessCallback) {
        if (textContentVertex == null) {
            return;
        }
        
        try {
            // Check if language has TTS configuration
            var languageVertex = textContentVertex.getLanguage();
            if (languageVertex == null) {
                return;
            }
            
            // Try to get voice configuration for the language
            var voiceQuery = new GetVoiceByLanguageIdQuery();
            voiceQuery.setLanguageId(languageVertex.getId());
            var voiceConfig = getVoiceByLanguageIdQueryHandler.handle(voiceQuery);
            
            // If no TTS configuration, skip
            if (voiceConfig == null) {
                return;
            }
            
            // Check if pronunciation already exists
            var existingPronunciations = textContentVertex.getPronunciations();
            if (existingPronunciations != null && !existingPronunciations.isEmpty()) {
                // Pronunciation already exists, skip generation
                return;
            }
            
            // Generate audio using TTS service
            var textToSpeechModel = TextToSpeechModel.create(
                    textContentVertex.getValue(),
                    voiceConfig);
            
            byte[] audioData = textToSpeechService.generateAudio(textToSpeechModel);
            if (audioData == null || audioData.length == 0) {
                return;
            }
            
            // Save audio file to storage
            var audioFileName = UUID.randomUUID().toString() + ".wav";
            binaryStorageService.set(audioFileName, audioData);
            
            // Create pronunciation vertex with audio content
            var command = new CreatePronunciationVertexCommand();
            var pronunciationId = UUID.randomUUID().toString();
            command.setId(pronunciationId);
            command.setSourceVertex(textContentVertex.getId());
            command.setAudioUrl(audioFileName);
            
            createPronunciationVertexCommandHandler.handle(command);
            
            // Invoke callback if provided
            if (onSuccessCallback != null) {
                var pronunciationVertex = PronunciationVertex.findById(
                        textContentVertex.getUnderlyingSource(), 
                        pronunciationId);
                if (pronunciationVertex != null) {
                    onSuccessCallback.accept(pronunciationVertex);
                }
            }
            
        } catch (Exception e) {
            // Log error but don't throw - this is async and shouldn't block
            System.err.println("Failed to generate TTS for text content " + 
                    textContentVertex.getId() + ": " + e.getMessage());
        }
    }
    
    /**
     * Checks if a text content vertex has a language with TTS configuration.
     * 
     * @param textContentVertex The text content vertex to check
     * @return true if TTS configuration exists for the language, false otherwise
     */
    public boolean hasTtsConfiguration(TextContentVertex textContentVertex) {
        if (textContentVertex == null) {
            return false;
        }
        
        var languageVertex = textContentVertex.getLanguage();
        if (languageVertex == null) {
            return false;
        }
        
        // Check if language has any TTS configuration (Google or Microsoft)
        var googleTtsConfigs = ConfigurationVertex.findByLanguageAndType(
                languageVertex, 
                ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        var microsoftTtsConfigs = ConfigurationVertex.findByLanguageAndType(
                languageVertex, 
                ConfigurationTypes.MICROSOFT_TEXT_TO_SPEECH);
        
        return !googleTtsConfigs.isEmpty() || !microsoftTtsConfigs.isEmpty();
    }
}
