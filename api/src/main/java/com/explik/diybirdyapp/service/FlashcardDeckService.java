package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.content.FlashcardDeckDto;
import com.explik.diybirdyapp.persistence.command.AddFlashcardDeckCommand;
import com.explik.diybirdyapp.persistence.command.DeleteFlashcardDeckCommand;
import com.explik.diybirdyapp.persistence.command.UpdateFlashcardDeckCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.GetAllFlashcardDecksQuery;
import com.explik.diybirdyapp.persistence.query.GetFlashcardDeckQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlashcardDeckService {
    @Autowired
    private QueryHandler<GetFlashcardDeckQuery, FlashcardDeckDto> getFlashcardDeckQueryHandler;

    @Autowired
    private QueryHandler<GetAllFlashcardDecksQuery, List<FlashcardDeckDto>> getAllFlashcardDecksQueryHandler;

    @Autowired
    private CommandHandler<AddFlashcardDeckCommand> addFlashcardDeckCommandHandler;

    @Autowired
    private CommandHandler<UpdateFlashcardDeckCommand> updateFlashcardDeckCommandHandler;

    @Autowired
    private CommandHandler<DeleteFlashcardDeckCommand> deleteFlashcardDeckCommandHandler;

    public FlashcardDeckDto add(String userId, FlashcardDeckDto model) {
        var command = new AddFlashcardDeckCommand();
        command.setUserId(userId);
        command.setDeckId(model.getId());
        command.setName(model.getName());
        command.setDescription(model.getDescription());
        addFlashcardDeckCommandHandler.handle(command);
        
        // Query the created deck
        var query = new GetFlashcardDeckQuery();
        query.setUserId(userId);
        query.setDeckId(command.getResultId());
        return getFlashcardDeckQueryHandler.handle(query);
    }

    public FlashcardDeckDto get(String userId, String id) {
        var query = new GetFlashcardDeckQuery();
        query.setUserId(userId);
        query.setDeckId(id);
        return getFlashcardDeckQueryHandler.handle(query);
    }

    public List<FlashcardDeckDto> getAll(String userId) {
        var query = new GetAllFlashcardDecksQuery();
        query.setUserId(userId);
        return getAllFlashcardDecksQueryHandler.handle(query);
    }

    public FlashcardDeckDto update(String userId, FlashcardDeckDto flashcardDeckModel) {
        var command = new UpdateFlashcardDeckCommand();
        command.setUserId(userId);
        command.setDeckId(flashcardDeckModel.getId());
        command.setName(flashcardDeckModel.getName());
        command.setDescription(flashcardDeckModel.getDescription());
        updateFlashcardDeckCommandHandler.handle(command);
        
        // Query the updated deck
        var query = new GetFlashcardDeckQuery();
        query.setUserId(userId);
        query.setDeckId(command.getResultId());
        return getFlashcardDeckQueryHandler.handle(query);
    }

    public void delete(String userId, String id) {
        var command = new DeleteFlashcardDeckCommand();
        command.setUserId(userId);
        command.setDeckId(id);
        deleteFlashcardDeckCommandHandler.handle(command);
    }
}
