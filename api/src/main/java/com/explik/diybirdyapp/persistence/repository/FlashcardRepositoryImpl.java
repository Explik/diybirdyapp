package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.*;
import com.explik.diybirdyapp.persistence.command.CreateFlashcardContentCommand;
import com.explik.diybirdyapp.persistence.command.UpdateFlashcardContentCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.modelFactory.FlashcardModelFactory;
import com.explik.diybirdyapp.persistence.query.GetAllFlashcardsQuery;
import com.explik.diybirdyapp.persistence.query.GetFlashcardByIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private CommandHandler<CreateFlashcardContentCommand> createFlashcardContentCommandHandler;

    @Autowired
    private CommandHandler<UpdateFlashcardContentCommand> updateFlashcardContentCommandHandler;

    @Autowired
    FlashcardModelFactory flashcardCardModelFactory;

    public FlashcardRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardDto add(FlashcardDto flashcardModel) {
        // Set ID if not provided
        if (flashcardModel.getId() == null) {
            flashcardModel.setId(UUID.randomUUID().toString());
        }

        // Create flashcard and all its content using the command
        var createCommand = new CreateFlashcardContentCommand();
        createCommand.setFlashcardDto(flashcardModel);
        createFlashcardContentCommandHandler.handle(createCommand);

        // Handle deck relations if needed
        var flashcardVertex = FlashcardVertex.findById(traversalSource, flashcardModel.getId());
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
        // Update flashcard and all its content using the command
        var updateCommand = new UpdateFlashcardContentCommand();
        updateCommand.setFlashcardDto(flashcardModel);
        updateFlashcardContentCommandHandler.handle(updateCommand);

        var flashcardVertex = FlashcardVertex.findById(traversalSource, flashcardModel.getId());
        return flashcardCardModelFactory.create(flashcardVertex);
    }

    private static FlashcardDeckVertex getFlashcardDeckVertex(GraphTraversalSource traversalSource, String deckId) {
        var vertex = traversalSource.V().has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, deckId).next();
        return new FlashcardDeckVertex(traversalSource, vertex);
    }
}
