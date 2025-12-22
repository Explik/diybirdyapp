package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.FlashcardDeckDto;
import com.explik.diybirdyapp.persistence.command.AddFlashcardDeckCommand;
import com.explik.diybirdyapp.persistence.command.DeleteFlashcardDeckCommand;
import com.explik.diybirdyapp.persistence.command.UpdateFlashcardDeckCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.generalCommand.SyncCommandHandler;
import com.explik.diybirdyapp.persistence.query.GetAllFlashcardDecksQuery;
import com.explik.diybirdyapp.persistence.query.GetFlashcardDeckQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlashcardDeckRepositoryImpl implements FlashcardDeckRepository {
    private final GraphTraversalSource traversalSource;

    @Autowired
    private QueryHandler<GetFlashcardDeckQuery, FlashcardDeckDto> getFlashcardDeckQueryHandler;

    @Autowired
    private QueryHandler<GetAllFlashcardDecksQuery, List<FlashcardDeckDto>> getAllFlashcardDecksQueryHandler;

    @Autowired
    private SyncCommandHandler<AddFlashcardDeckCommand, FlashcardDeckDto> addFlashcardDeckCommandHandler;

    @Autowired
    private SyncCommandHandler<UpdateFlashcardDeckCommand, FlashcardDeckDto> updateFlashcardDeckCommandHandler;

    @Autowired
    private CommandHandler<DeleteFlashcardDeckCommand> deleteFlashcardDeckCommandHandler;

    public FlashcardDeckRepositoryImpl(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public FlashcardDeckDto add(String userId, FlashcardDeckDto model) {
        var command = new AddFlashcardDeckCommand();
        command.setUserId(userId);
        command.setDeckId(model.getId());
        command.setName(model.getName());
        command.setDescription(model.getDescription());
        return addFlashcardDeckCommandHandler.handle(command);
    }

    @Override
    public FlashcardDeckDto get(String userId, String id) {
        var query = new GetFlashcardDeckQuery();
        query.setUserId(userId);
        query.setDeckId(id);
        return getFlashcardDeckQueryHandler.handle(query);
    }

    @Override
    public List<FlashcardDeckDto> getAll(String userId) {
        var query = new GetAllFlashcardDecksQuery();
        query.setUserId(userId);
        return getAllFlashcardDecksQueryHandler.handle(query);
    }

    @Override
    public FlashcardDeckDto update(String userId, FlashcardDeckDto flashcardDeckModel) {
        var command = new UpdateFlashcardDeckCommand();
        command.setUserId(userId);
        command.setDeckId(flashcardDeckModel.getId());
        command.setName(flashcardDeckModel.getName());
        command.setDescription(flashcardDeckModel.getDescription());
        return updateFlashcardDeckCommandHandler.handle(command);
    }

    @Override
    public void delete(String userId, String id) {
        var command = new DeleteFlashcardDeckCommand();
        command.setUserId(userId);
        command.setDeckId(id);
        deleteFlashcardDeckCommandHandler.handle(command);
    }
}
