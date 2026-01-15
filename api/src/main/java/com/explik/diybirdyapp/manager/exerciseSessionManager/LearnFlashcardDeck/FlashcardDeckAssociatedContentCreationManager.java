package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.manager.contentCreationManager.TextToSpeechContentCreationManager;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Associated Content Creation Manager - Dispatches async content creation tasks to create associated 
 * content for the flashcards selected by the crawler.
 * 
 * Depending on the session settings, the associated content may include auto-generated transcriptions, 
 * pronunciation, etc. The manager uses a set of content creation strategies to create the associated 
 * content using the ContentCreationContext.
 * 
 * This manager dispatches text-to-speech generation for text content vertices that have a language
 * with TTS configuration. The pronunciation is saved as a PronunciationVertex connected to an
 * AudioContentVertex on the graph.
 */
@Component
public class FlashcardDeckAssociatedContentCreationManager {
    
    @Autowired
    private TextToSpeechContentCreationManager textToSpeechContentCreationManager;
    
    /**
     * Dispatches async content creation for a list of content vertices.
     * Currently focuses on text-to-speech generation for text content.
     * 
     * @param contentVertices List of content vertices to process
     */
    public void dispatchContentCreation(List<ContentVertex> contentVertices) {
        if (contentVertices == null || contentVertices.isEmpty()) {
            return;
        }
        
        for (ContentVertex contentVertex : contentVertices) {
            // Check if this is a TextContentVertex
            if (contentVertex instanceof TextContentVertex textContentVertex) {
                // Check if TTS configuration exists for this language
                if (textToSpeechContentCreationManager.hasTtsConfiguration(textContentVertex)) {
                    // Dispatch async TTS generation
                    textToSpeechContentCreationManager.dispatchTextToSpeechGeneration(textContentVertex);
                }
            }
        }
    }
}
