package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.content.FileUploadModel;
import com.explik.diybirdyapp.model.content.FlashcardDto;
import com.explik.diybirdyapp.persistence.command.CreateFlashcardContentCommand;
import com.explik.diybirdyapp.persistence.command.UpdateFlashcardContentCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.modelFactory.FlashcardModelFactory;
import com.explik.diybirdyapp.persistence.query.GetAllFlashcardsQuery;
import com.explik.diybirdyapp.persistence.query.GetFlashcardByIdQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import com.explik.diybirdyapp.persistence.vertex.FlashcardDeckVertex;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FlashcardService {
    @Autowired
    BinaryStorageService binaryStorageService;

    @Autowired
    private GraphTraversalSource traversalSource;

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

    public FlashcardDto add(FlashcardDto model, MultipartFile[] files) {
        validateFiles(model, files);
        saveFilesIfAny(files);

        // Set ID if not provided
        if (model.getId() == null) {
            model.setId(UUID.randomUUID().toString());
        }

        // Create flashcard and all its content using the command
        var createCommand = new CreateFlashcardContentCommand();
        createCommand.setFlashcardDto(model);
        createFlashcardContentCommandHandler.handle(createCommand);

        // Handle deck relations if needed
        var flashcardVertex = FlashcardVertex.findById(traversalSource, model.getId());
        if (model.getDeckId() != null) {
            var flashcardDeckVertex = getFlashcardDeckVertex(model.getDeckId());
            flashcardDeckVertex.addFlashcard(flashcardVertex);
        }
        if (model.getDeckOrder() != null) {
            flashcardVertex.setDeckOrder(model.getDeckOrder());
        }

        return flashcardCardModelFactory.create(flashcardVertex);
    }

    public FlashcardDto update(FlashcardDto model, MultipartFile[] files) {
        validateFiles(model, files);
        saveFilesIfAny(files);

        // Update flashcard and all its content using the command
        var updateCommand = new UpdateFlashcardContentCommand();
        updateCommand.setFlashcardDto(model);
        updateFlashcardContentCommandHandler.handle(updateCommand);

        var flashcardVertex = FlashcardVertex.findById(traversalSource, model.getId());
        return flashcardCardModelFactory.create(flashcardVertex);
    }

    public void delete(String id) {
        var flashcardVertex = FlashcardVertex.findById(traversalSource, id);

        // Remove from deck
        var deck = flashcardVertex.getDeck();
        if (deck != null)
            deck.removeFlashcard(flashcardVertex);

        // TODO Remove vertex and content vertices
    }

    public FlashcardDto get(String id) {
        var query = new GetFlashcardByIdQuery();
        query.setId(id);
        return getFlashcardByIdQueryHandler.handle(query);
    }

    public List<FlashcardDto> getAll(@Nullable String setId) {
        var query = new GetAllFlashcardsQuery();
        query.setDeckId(setId);
        return getAllFlashcardsQueryHandler.handle(query);
    }

    private FlashcardDeckVertex getFlashcardDeckVertex(String deckId) {
        var vertex = traversalSource.V().has(FlashcardDeckVertex.LABEL, FlashcardDeckVertex.PROPERTY_ID, deckId).next();
        return new FlashcardDeckVertex(traversalSource, vertex);
    }

    private void validateFiles(FlashcardDto model, MultipartFile[] files) {
        // Extract all file names from the model
        List<String> expectedFileNames = new ArrayList<>();

        if (model.getFrontContent() instanceof FileUploadModel frontFileUpload)
            expectedFileNames.addAll(frontFileUpload.getFileNames());
        if (model.getBackContent() instanceof FileUploadModel backFileUpload)
            expectedFileNames.addAll(backFileUpload.getFileNames());

        // Verify number of files
        if (files == null && expectedFileNames.isEmpty())
            return; // No files expected

        if (files.length != expectedFileNames.size())
            throw new IllegalArgumentException("Number of files does not match the number of expected files");

        // Verify file names
        for (MultipartFile file : files) {
            if (!expectedFileNames.contains(file.getOriginalFilename()))
                throw new IllegalArgumentException("File name does not match any expected file names");
        }
    }

    private void saveFilesIfAny(MultipartFile[] files) {
        if (files == null)
            return;

        try {
            for (MultipartFile file : files) {
                binaryStorageService.set(file.getOriginalFilename(), file.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }
}
