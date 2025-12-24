package com.explik.diybirdyapp.persistence.command.helper;

import com.explik.diybirdyapp.model.content.*;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import java.util.UUID;

/**
 * Helper class for creating content vertices from FlashcardContentDto models.
 * This class provides shared functionality for creating different types of content vertices
 * without relying on other command handlers.
 */
public class ContentVertexCommandHelper {
    
    /**
     * Creates a content vertex based on the type of FlashcardContentDto provided.
     * 
     * @param traversalSource the graph traversal source
     * @param model the content model to create a vertex from
     * @return the created ContentVertex
     * @throws IllegalArgumentException if the content type is not supported
     */
    public static ContentVertex createContentVertex(GraphTraversalSource traversalSource, FlashcardContentDto model) {
        if (model instanceof FlashcardContentTextDto) {
            return createTextContentVertex(traversalSource, (FlashcardContentTextDto) model);
        }
        else if (model instanceof FlashcardContentUploadAudioDto) {
            return createAudioContentVertex(traversalSource, (FlashcardContentUploadAudioDto) model);
        }
        else if (model instanceof FlashcardContentUploadImageDto) {
            return createImageContentVertex(traversalSource, (FlashcardContentUploadImageDto) model);
        }
        else if (model instanceof FlashcardContentUploadVideoDto) {
            return createVideoContentVertex(traversalSource, (FlashcardContentUploadVideoDto) model);
        }
        throw new IllegalArgumentException("Unsupported content type: " + model.getClass().getName());
    }

    /**
     * Creates a text content vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param model the text content model
     * @return the created TextContentVertex
     */
    private static TextContentVertex createTextContentVertex(GraphTraversalSource traversalSource, FlashcardContentTextDto model) {
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());
        var id = UUID.randomUUID().toString();
        
        var vertex = TextContentVertex.create(traversalSource);
        vertex.setId(id);
        vertex.setValue(model.getText() != null ? model.getText() : "");
        vertex.setLanguage(languageVertex);
        
        return vertex;
    }

    /**
     * Creates an audio content vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param model the audio content model
     * @return the created AudioContentVertex
     */
    private static AudioContentVertex createAudioContentVertex(GraphTraversalSource traversalSource, FlashcardContentUploadAudioDto model) {
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());
        var id = UUID.randomUUID().toString();
        var url = model.getAudioFileName();
        
        var vertex = AudioContentVertex.create(traversalSource);
        vertex.setId(id);
        vertex.setUrl(url);
        vertex.setLanguage(languageVertex);
        
        return vertex;
    }

    /**
     * Creates an image content vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param model the image content model
     * @return the created ImageContentVertex
     */
    private static ImageContentVertex createImageContentVertex(GraphTraversalSource traversalSource, FlashcardContentUploadImageDto model) {
        var id = UUID.randomUUID().toString();
        var url = model.getImageFileName();
        
        var vertex = ImageContentVertex.create(traversalSource);
        vertex.setId(id);
        vertex.setUrl(url);
        
        return vertex;
    }

    /**
     * Creates a video content vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param model the video content model
     * @return the created VideoContentVertex
     */
    private static VideoContentVertex createVideoContentVertex(GraphTraversalSource traversalSource, FlashcardContentUploadVideoDto model) {
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());
        var id = UUID.randomUUID().toString();
        var url = model.getVideoFileName();
        
        var vertex = VideoContentVertex.create(traversalSource);
        vertex.setId(id);
        vertex.setUrl(url);
        vertex.setLanguage(languageVertex);
        
        return vertex;
    }

    /**
     * Creates or updates a content vertex based on the type of FlashcardContentDto provided.
     * If the model represents an existing content (has an ID and is not an upload type),
     * it updates the existing vertex. Otherwise, it creates a new vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param model the content model
     * @param existingVertex the existing content vertex (may be null)
     * @return the created or updated ContentVertex
     */
    public static ContentVertex createOrUpdateContentVertex(GraphTraversalSource traversalSource, 
                                                            FlashcardContentDto model, 
                                                            ContentVertex existingVertex) {
        if (model instanceof FlashcardContentTextDto) {
            return updateTextContentVertex(traversalSource, (TextContentVertex) existingVertex, (FlashcardContentTextDto) model);
        }
        else if (model instanceof FlashcardContentAudioDto) {
            return updateAudioContentVertex(traversalSource, (AudioContentVertex) existingVertex, (FlashcardContentAudioDto) model);
        }
        else if (model instanceof FlashcardContentUploadAudioDto) {
            return createAudioContentVertex(traversalSource, (FlashcardContentUploadAudioDto) model);
        }
        else if (model instanceof FlashcardContentImageDto) {
            return updateImageContentVertex(traversalSource, (ImageContentVertex) existingVertex, (FlashcardContentImageDto) model);
        }
        else if (model instanceof FlashcardContentUploadImageDto) {
            return createImageContentVertex(traversalSource, (FlashcardContentUploadImageDto) model);
        }
        else if (model instanceof FlashcardContentVideoDto) {
            return updateVideoContentVertex(traversalSource, (VideoContentVertex) existingVertex, (FlashcardContentVideoDto) model);
        }
        else if (model instanceof FlashcardContentUploadVideoDto) {
            return createVideoContentVertex(traversalSource, (FlashcardContentUploadVideoDto) model);
        }
        return existingVertex;
    }

    /**
     * Updates an existing text content vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param vertex the existing text content vertex
     * @param model the text content model
     * @return the updated TextContentVertex
     */
    private static TextContentVertex updateTextContentVertex(GraphTraversalSource traversalSource, 
                                                             TextContentVertex vertex, 
                                                             FlashcardContentTextDto model) {
        if (model.getLanguageId() != null) {
            var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());
            vertex.setLanguage(languageVertex);
        }
        if (model.getText() != null) {
            vertex.setValue(model.getText());
        }
        
        return vertex;
    }

    /**
     * Updates an existing audio content vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param vertex the existing audio content vertex
     * @param model the audio content model
     * @return the updated AudioContentVertex
     */
    private static AudioContentVertex updateAudioContentVertex(GraphTraversalSource traversalSource, 
                                                               AudioContentVertex vertex, 
                                                               FlashcardContentAudioDto model) {
        if (model.getAudioUrl() != null) {
            vertex.setUrl(model.getAudioUrl());
        }
        
        return vertex;
    }

    /**
     * Updates an existing image content vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param vertex the existing image content vertex
     * @param model the image content model
     * @return the updated ImageContentVertex
     */
    private static ImageContentVertex updateImageContentVertex(GraphTraversalSource traversalSource, 
                                                               ImageContentVertex vertex, 
                                                               FlashcardContentImageDto model) {
        if (model.getImageUrl() != null) {
            vertex.setUrl(model.getImageUrl());
        }
        
        return vertex;
    }

    /**
     * Updates an existing video content vertex.
     * 
     * @param traversalSource the graph traversal source
     * @param vertex the existing video content vertex
     * @param model the video content model
     * @return the updated VideoContentVertex
     */
    private static VideoContentVertex updateVideoContentVertex(GraphTraversalSource traversalSource, 
                                                               VideoContentVertex vertex, 
                                                               FlashcardContentVideoDto model) {
        if (model.getVideoUrl() != null) {
            vertex.setUrl(model.getVideoUrl());
        }
        
        return vertex;
    }

    /**
     * Retrieves a language vertex by its ID.
     * 
     * @param traversalSource the graph traversal source
     * @param languageId the language ID
     * @return the LanguageVertex
     * @throws IllegalArgumentException if the language is not found
     */
    private static LanguageVertex getLanguageVertex(GraphTraversalSource traversalSource, String languageId) {
        var vertex = traversalSource.V().has(LanguageVertex.LABEL, LanguageVertex.PROPERTY_ID, languageId).tryNext();
        if (vertex.isEmpty()) {
            throw new IllegalArgumentException("Language not found: " + languageId);
        }
        return new LanguageVertex(traversalSource, vertex.get());
    }
}
