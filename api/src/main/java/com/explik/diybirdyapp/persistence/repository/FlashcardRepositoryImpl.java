package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.*;
import com.explik.diybirdyapp.persistence.modelFactory.FlashcardModelFactory;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.vertexFactory.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FlashcardRepositoryImpl implements FlashcardRepository {
    private final GraphTraversalSource traversalSource;

    @Autowired
    AudioContentVertexFactory audioContentVertexFactory;

    @Autowired
    ImageContentVertexFactory imageContentVertexFactory;

    @Autowired
    TextContentVertexFactory textContentVertexFactory;

    @Autowired
    VideoContentVertexFactory videoContentVertexFactory;

    @Autowired
    FlashcardVertexFactory flashcardVertexFactory;

    @Autowired
    FlashcardModelFactory flashcardCardModelFactory;

    public FlashcardRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardDto add(FlashcardDto flashcardModel) {
        if (flashcardModel.getFrontContent() == null)
            throw new IllegalArgumentException("Flashcard is missing front content");
        if (flashcardModel.getBackContent() == null)
            throw new IllegalArgumentException("Flashcard is missing back content");

        var leftContentVertex = createContent(flashcardModel.getFrontContent());
        var rightContentVertex = createContent(flashcardModel.getBackContent());
        var flashcardVertex = flashcardVertexFactory.create(traversalSource, new FlashcardVertexFactory.Options(UUID.randomUUID().toString(), leftContentVertex, rightContentVertex));

        if (flashcardModel.getDeckId() != null) {
            var flashcardDeckVertex = getFlashcardDeckVertex(traversalSource, flashcardModel.getDeckId());
            flashcardDeckVertex.addFlashcard(flashcardVertex);
        }
        if (flashcardModel.getDeckOrder() != null) {
            flashcardVertex.setDeckOrder(flashcardModel.getDeckOrder());
        }

        return flashcardCardModelFactory.create(flashcardVertex);
    }

    @Override
    public void delete(String id) {
        var flashcardVertex = FlashcardVertex.findById(traversalSource, id);

        // Remove from deck
        var deck = flashcardVertex.getDeck();
        if (deck != null)
            deck.removeFlashcard(flashcardVertex);

        // TODO Remove vertex and content vertices
    }

    @Override
    public FlashcardDto get(String id) {
        var vertex = FlashcardVertex.findById(traversalSource, id);
        return flashcardCardModelFactory.create(vertex);
    }

    @Override
    public List<FlashcardDto> getAll(String deckId) {
        List<FlashcardVertex> vertices;

        if (deckId != null) {
            vertices = FlashcardVertex.findByDeckId(traversalSource, deckId);
        }
        else {
            vertices = FlashcardVertex.findAll(traversalSource);
        }

        return vertices
            .stream()
            .map(v -> flashcardCardModelFactory.create(v))
            .toList();
    }

    @Override
    public FlashcardDto update(FlashcardDto flashcardModel) {
        if (flashcardModel.getId() == null)
            throw new IllegalArgumentException("Flashcard is missing id");

        var flashcardVertex = FlashcardVertex.findById(traversalSource, flashcardModel.getId());
        if (flashcardVertex == null)
            throw new IllegalArgumentException("Flashcard not found");

        // Alter deck relation
        if (flashcardModel.getDeckId() != null) {
            flashcardVertex.getDeck().removeFlashcard(flashcardVertex);

            var flashcardDeckVertex = getFlashcardDeckVertex(traversalSource, flashcardModel.getDeckId());
            flashcardDeckVertex.addFlashcard(flashcardVertex);
        }
        if (flashcardModel.getDeckOrder() != null) {
            flashcardVertex.setDeckOrder(flashcardModel.getDeckOrder());
        }

        // Alter left content
        var leftModel = flashcardModel.getFrontContent();
        var leftVertex = flashcardVertex.getLeftContent();
        leftVertex = createOrUpdateContent(leftModel, leftVertex);
        flashcardVertex.setLeftContent(leftVertex);

        // Alter right content
        var rightModel = flashcardModel.getBackContent();
        var rightVertex = flashcardVertex.getRightContent();
        rightVertex = createOrUpdateContent(rightModel, rightVertex);
        flashcardVertex.setRightContent(rightVertex);

        return flashcardCardModelFactory.create(flashcardVertex);
    }

    private ContentVertex createContent(FlashcardContentDto model) {
        if (model instanceof FlashcardContentTextDto) {
            return createTextContent((FlashcardContentTextDto) model);
        }
        else if (model instanceof FlashcardContentUploadAudioDto) {
            return createAudioContent((FlashcardContentUploadAudioDto) model);
        }
        else if (model instanceof FlashcardContentUploadImageDto) {
            return createImageContent((FlashcardContentUploadImageDto) model);
        }
        else if (model instanceof FlashcardContentUploadVideoDto) {
            return createVideoContent((FlashcardContentUploadVideoDto) model);
        }
        throw new RuntimeException("Not implemented");
    }

    private ContentVertex createOrUpdateContent(FlashcardContentDto model, ContentVertex vertex) {
        if (model instanceof FlashcardContentTextDto) {
            return updateTextContent((TextContentVertex) vertex, (FlashcardContentTextDto) model);
        }
        else if (model instanceof FlashcardContentAudioDto) {
            return updateAudioContent((AudioContentVertex) vertex, (FlashcardContentAudioDto) model);
        }
        else if (model instanceof FlashcardContentUploadAudioDto) {
            return createAudioContent((FlashcardContentUploadAudioDto) model);
        }
        else if (model instanceof FlashcardContentImageDto) {
            return updateImageContent((ImageContentVertex) vertex, (FlashcardContentImageDto) model);
        }
        else if (model instanceof FlashcardContentUploadImageDto) {
            return createImageContent((FlashcardContentUploadImageDto) model);
        }
        else if (model instanceof FlashcardContentVideoDto) {
            return updateVideoContent((VideoContentVertex) vertex, (FlashcardContentVideoDto) model);
        }
        else if(model instanceof FlashcardContentUploadVideoDto) {
            return createVideoContent((FlashcardContentUploadVideoDto) model);
        }
        return vertex;
    }

    private AudioContentVertex createAudioContent(FlashcardContentUploadAudioDto model) {
        var url = model.getAudioFileName();
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());

        return audioContentVertexFactory.create(
                traversalSource,
                new AudioContentVertexFactory.Options(UUID.randomUUID().toString(), url, languageVertex));
    }

    private AudioContentVertex updateAudioContent(AudioContentVertex vertex, FlashcardContentAudioDto model) {
        if (vertex.isStatic()) {
            var newVertex = audioContentVertexFactory.copy(vertex);
            newVertex.setUrl(model.getAudioUrl());
            return newVertex;
        }
        else {
            vertex.setUrl(model.getAudioUrl());
            return vertex;
        }
    }

    private ImageContentVertex createImageContent(FlashcardContentUploadImageDto model) {
        var url = model.getImageFileName();

        return imageContentVertexFactory.create(
                traversalSource,
                new ImageContentVertexFactory.Options(UUID.randomUUID().toString(), url));
    }

    private ImageContentVertex updateImageContent(ImageContentVertex vertex, FlashcardContentImageDto model) {
        if (vertex.isStatic()) {
            var newVertex = imageContentVertexFactory.copy(vertex);
            newVertex.setUrl(model.getImageUrl());
            return newVertex;
        }
        else {
            vertex.setUrl(model.getImageUrl());
            return vertex;
        }
    }

    private TextContentVertex createTextContent(FlashcardContentTextDto model) {
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());
        var textContentVertex = TextContentVertex.create(traversalSource);
        textContentVertex.setId(UUID.randomUUID().toString());
        textContentVertex.setLanguage(languageVertex);
        textContentVertex.setValue(model.getText() != null ? model.getText() : "");

        return textContentVertex;
    }

    private TextContentVertex updateTextContent(TextContentVertex vertex, FlashcardContentTextDto model) {
        var updateVertex = vertex.isStatic() ? textContentVertexFactory.copy(vertex) : vertex;

        if (model.getLanguageId() != null) {
            var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());
            updateVertex.setLanguage(languageVertex);
        }
        if (model.getText() != null) {
            updateVertex.setValue(model.getText());
        }
        return updateVertex;
    }

    private VideoContentVertex createVideoContent(FlashcardContentUploadVideoDto model) {
        var url = model.getVideoFileName();
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());

        return videoContentVertexFactory.create(
                traversalSource,
                new VideoContentVertexFactory.Options(UUID.randomUUID().toString(), url, languageVertex));
    }

    private VideoContentVertex updateVideoContent(VideoContentVertex vertex, FlashcardContentVideoDto model) {
        if (vertex.isStatic()) {
            var newVertex = videoContentVertexFactory.copy(vertex);
            newVertex.setUrl(model.getVideoUrl());
            return newVertex;
        }
        else {
            vertex.setUrl(model.getVideoUrl());
            return vertex;
        }
    }

    private static LanguageVertex getLanguageVertex(GraphTraversalSource traversalSource, String languageId) {
        var vertex = traversalSource.V().has(LanguageVertex.LABEL, LanguageVertex.PROPERTY_ID, languageId).next();
        return new LanguageVertex(traversalSource, vertex);
    }

    private static FlashcardDeckVertex getFlashcardDeckVertex(GraphTraversalSource traversalSource, String deckId) {
        var vertex = traversalSource.V().has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, deckId).next();
        return new FlashcardDeckVertex(traversalSource, vertex);
    }
}
