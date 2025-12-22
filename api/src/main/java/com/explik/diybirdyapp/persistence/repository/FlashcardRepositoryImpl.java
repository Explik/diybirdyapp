package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.*;
import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateFlashcardVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateImageContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateTextContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreateVideoContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.UpdateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.UpdateFlashcardVertexCommand;
import com.explik.diybirdyapp.persistence.command.UpdateImageContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.UpdateTextContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.UpdateVideoContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.modelFactory.FlashcardModelFactory;
import com.explik.diybirdyapp.persistence.query.GetAllFlashcardsQuery;
import com.explik.diybirdyapp.persistence.query.GetFlashcardByIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.vertexFactory.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;
import java.util.UUID;

@Component
public class FlashcardRepositoryImpl implements FlashcardRepository {
    private final GraphTraversalSource traversalSource;

    @Autowired
    private QueryHandler<GetFlashcardByIdQuery, FlashcardDto> getFlashcardByIdQueryHandler;

    @Autowired
    private QueryHandler<GetAllFlashcardsQuery, List<FlashcardDto>> getAllFlashcardsQueryHandler;

    @Autowired
    CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandHandler;

    @Autowired
    CommandHandler<UpdateAudioContentVertexCommand> updateAudioContentVertexCommandHandler;

    @Autowired
    CommandHandler<CreateImageContentVertexCommand> createImageContentVertexCommandHandler;

    @Autowired
    CommandHandler<UpdateImageContentVertexCommand> updateImageContentVertexCommandHandler;

    @Autowired
    CommandHandler<CreateTextContentVertexCommand> createTextContentVertexCommandHandler;

    @Autowired
    CommandHandler<UpdateTextContentVertexCommand> updateTextContentVertexCommandHandler;

    @Autowired
    CommandHandler<CreateVideoContentVertexCommand> createVideoContentVertexCommandHandler;

    @Autowired
    CommandHandler<UpdateVideoContentVertexCommand> updateVideoContentVertexCommandHandler;

    @Autowired
    CommandHandler<CreateFlashcardVertexCommand> createFlashcardVertexCommandHandler;

    @Autowired
    CommandHandler<UpdateFlashcardVertexCommand> updateFlashcardVertexCommandHandler;

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
        
        var id = UUID.randomUUID().toString();
        var createCommand = new CreateFlashcardVertexCommand();
        createCommand.setId(id);
        createCommand.setLeftContent(leftContentVertex);
        createCommand.setRightContent(rightContentVertex);
        createFlashcardVertexCommandHandler.handle(createCommand);
        var flashcardVertex = FlashcardVertex.findById(traversalSource, id);

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
        var query = new GetFlashcardByIdQuery();
        query.setId(id);
        return getFlashcardByIdQueryHandler.handle(query);
    }

    @Override
    public List<FlashcardDto> getAll(String deckId) {
        var query = new GetAllFlashcardsQuery();
        query.setDeckId(deckId);
        return getAllFlashcardsQueryHandler.handle(query);
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

        var id = UUID.randomUUID().toString();
        var createCommand = new CreateAudioContentVertexCommand();
        createCommand.setId(id);
        createCommand.setUrl(url);
        createCommand.setLanguageVertexId(languageVertex.getId());
        createAudioContentVertexCommandHandler.handle(createCommand);

        return AudioContentVertex.getById(traversalSource, id);
    }

    private AudioContentVertex updateAudioContent(AudioContentVertex vertex, FlashcardContentAudioDto model) {
        var updateCommand = new UpdateAudioContentVertexCommand();
        updateCommand.setId(model.getId());
        updateCommand.setUrl(model.getAudioUrl());
        updateAudioContentVertexCommandHandler.handle(updateCommand);

        return AudioContentVertex.getById(traversalSource, model.getId());
    }

    private ImageContentVertex createImageContent(FlashcardContentUploadImageDto model) {
        var url = model.getImageFileName();

        var id = UUID.randomUUID().toString();
        var createCommand = new CreateImageContentVertexCommand();
        createCommand.setId(id);
        createCommand.setUrl(url);
        createImageContentVertexCommandHandler.handle(createCommand);

        return ImageContentVertex.findById(traversalSource, id);
    }

    private ImageContentVertex updateImageContent(ImageContentVertex vertex, FlashcardContentImageDto model) {
        var updateCommand = new UpdateImageContentVertexCommand();
        updateCommand.setId(model.getId());
        updateCommand.setUrl(model.getImageUrl());
        updateImageContentVertexCommandHandler.handle(updateCommand);

        return ImageContentVertex.findById(traversalSource, model.getId());
    }

    private TextContentVertex createTextContent(FlashcardContentTextDto model) {
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());
        var id = UUID.randomUUID().toString();
        var createCommand = new CreateTextContentVertexCommand();
        createCommand.setId(id);
        createCommand.setLanguage(languageVertex);
        createCommand.setValue(model.getText() != null ? model.getText() : "");
        createTextContentVertexCommandHandler.handle(createCommand);

        return TextContentVertex.findById(traversalSource, id);
    }

    private TextContentVertex updateTextContent(TextContentVertex vertex, FlashcardContentTextDto model) {
        var updateCommand = new UpdateTextContentVertexCommand();
        updateCommand.setId(vertex.getId());
        
        if (model.getLanguageId() != null) {
            var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());
            updateCommand.setLanguage(languageVertex);
        }
        if (model.getText() != null) {
            updateCommand.setValue(model.getText());
        }
        updateTextContentVertexCommandHandler.handle(updateCommand);
        
        return TextContentVertex.findById(traversalSource, vertex.getId());
    }

    private VideoContentVertex createVideoContent(FlashcardContentUploadVideoDto model) {
        var url = model.getVideoFileName();
        var languageVertex = getLanguageVertex(traversalSource, model.getLanguageId());

        var id = UUID.randomUUID().toString();
        var createCommand = new CreateVideoContentVertexCommand();
        createCommand.setId(id);
        createCommand.setUrl(url);
        createCommand.setLanguageVertex(languageVertex);
        createVideoContentVertexCommandHandler.handle(createCommand);

        return VideoContentVertex.getById(traversalSource, id);
    }

    private VideoContentVertex updateVideoContent(VideoContentVertex vertex, FlashcardContentVideoDto model) {
        var updateCommand = new UpdateVideoContentVertexCommand();
        updateCommand.setId(model.getId());
        updateCommand.setUrl(model.getVideoUrl());
        updateVideoContentVertexCommandHandler.handle(updateCommand);

        return VideoContentVertex.getById(traversalSource, model.getId());
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
