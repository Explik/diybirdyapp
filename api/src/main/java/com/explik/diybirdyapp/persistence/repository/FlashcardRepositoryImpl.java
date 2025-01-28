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
    public FlashcardModel add(FlashcardModel flashcardModel) {
        var leftContentVertex = createContent(flashcardModel.getFrontContent());
        var rightContentVertex = createContent(flashcardModel.getBackContent());
        var flashcardVertex = flashcardVertexFactory.create(traversalSource, new FlashcardVertexFactory.Options(UUID.randomUUID().toString(), leftContentVertex, rightContentVertex));

        if (flashcardModel.getDeckId() != null) {
            var flashcardDeckVertex = getFlashcardDeckVertex(traversalSource, flashcardModel.getDeckId());
            flashcardDeckVertex.addFlashcard(flashcardVertex);
        }

        return flashcardCardModelFactory.create(flashcardVertex);
    }

    @Override
    public FlashcardModel get(String id) {
        var vertex = FlashcardVertex.findById(traversalSource, id);
        return flashcardCardModelFactory.create(vertex);
    }

    @Override
    public List<FlashcardModel> getAll(String deckId) {
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
    public FlashcardModel update(FlashcardModel flashcardModel) {
        if (flashcardModel.getId() == null)
            throw new IllegalArgumentException("Flashcard is missing id");

        var flashcardVertex = FlashcardVertex.findById(traversalSource, flashcardModel.getId());
        if (flashcardVertex == null)
            throw new IllegalArgumentException("Flashcard not found");

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

    private ContentVertex createContent(FlashcardContentModel model) {
        if (model instanceof FlashcardContentTextModel) {
            return createTextContent((FlashcardContentTextModel) model);
        }
        else if (model instanceof FlashcardContentUploadAudioModel) {
            return createAudioContent((FlashcardContentUploadAudioModel) model);
        }
        else if (model instanceof FlashcardContentUploadImageModel) {
            return createImageContent((FlashcardContentUploadImageModel) model);
        }
        else if (model instanceof FlashcardContentUploadVideoModel) {
            return createVideoContent((FlashcardContentUploadVideoModel) model);
        }
        throw new RuntimeException("Not implemented");
    }

    private ContentVertex createOrUpdateContent(FlashcardContentModel model, ContentVertex vertex) {
        if (model instanceof FlashcardContentTextModel) {
            return updateTextContent((TextContentVertex) vertex, (FlashcardContentTextModel) model);
        }
        else if (model instanceof FlashcardContentAudioModel) {
            return updateAudioContent((AudioContentVertex) vertex, (FlashcardContentAudioModel) model);
        }
        else if (model instanceof FlashcardContentUploadAudioModel) {
            return createAudioContent((FlashcardContentUploadAudioModel) model);
        }
        else if (model instanceof FlashcardContentImageModel) {
            return updateImageContent((ImageContentVertex) vertex, (FlashcardContentImageModel) model);
        }
        else if (model instanceof FlashcardContentUploadImageModel) {
            return createImageContent((FlashcardContentUploadImageModel) model);
        }
        else if (model instanceof FlashcardContentVideoModel) {
            return updateVideoContent((VideoContentVertex) vertex, (FlashcardContentVideoModel) model);
        }
        else if(model instanceof FlashcardContentUploadVideoModel) {
            return createVideoContent((FlashcardContentUploadVideoModel) model);
        }
        return vertex;
    }

    private AudioContentVertex createAudioContent(FlashcardContentUploadAudioModel model) {
        var url = "http://localhost:8080/file/" + model.getAudioFileName();
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());

        return audioContentVertexFactory.create(
                traversalSource,
                new AudioContentVertexFactory.Options(UUID.randomUUID().toString(), url, languageVertex));
    }

    private AudioContentVertex updateAudioContent(AudioContentVertex vertex, FlashcardContentAudioModel model) {
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

    private ImageContentVertex createImageContent(FlashcardContentUploadImageModel model) {
        var url = "http://localhost:8080/file/" + model.getImageFileName();

        return imageContentVertexFactory.create(
                traversalSource,
                new ImageContentVertexFactory.Options(UUID.randomUUID().toString(), url));
    }

    private ImageContentVertex updateImageContent(ImageContentVertex vertex, FlashcardContentImageModel model) {
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

    private TextContentVertex createTextContent(FlashcardContentTextModel model) {
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());
        var textContentVertex = TextContentVertex.create(traversalSource);
        textContentVertex.setId(UUID.randomUUID().toString());
        textContentVertex.setLanguage(languageVertex);
        textContentVertex.setValue(model.getText() != null ? model.getText() : "");

        return textContentVertex;
    }

    private TextContentVertex updateTextContent(TextContentVertex vertex, FlashcardContentTextModel model) {
        if (vertex.isStatic()) {
            var newVertex = textContentVertexFactory.copy(vertex);
            newVertex.setValue(model.getText());
            return newVertex;
        }
        else {
            vertex.setValue(model.getText());
            return vertex;
        }
    }

    private VideoContentVertex createVideoContent(FlashcardContentUploadVideoModel model) {
        var url = "http://localhost:8080/file/" + model.getVideoFileName();
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());

        return videoContentVertexFactory.create(
                traversalSource,
                new VideoContentVertexFactory.Options(UUID.randomUUID().toString(), url, languageVertex));
    }

    private VideoContentVertex updateVideoContent(VideoContentVertex vertex, FlashcardContentVideoModel model) {
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
