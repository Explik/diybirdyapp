package com.explik.diybirdyapp.command;

import com.explik.diybirdyapp.command.dto.ImportFlashcardDeckDTO;
import com.explik.diybirdyapp.model.content.FlashcardDeckModel;
import com.explik.diybirdyapp.model.content.FlashcardLanguageModel;
import com.explik.diybirdyapp.model.content.FlashcardModel;
import com.explik.diybirdyapp.persistence.repository.FlashcardDeckRepository;
import com.explik.diybirdyapp.persistence.repository.FlashcardRepository;
import com.explik.diybirdyapp.persistence.repository.FlashcardLanguageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Component
@CommandLine.Command(name = "import-flashcard-deck", description = "Import a flashcard deck from a file")
public class ImportFlashcardDeckCommand implements Runnable {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public FlashcardDeckRepository flashcardDeckRepository;

    @Autowired
    public FlashcardRepository flashcardCardRepository;

    @Autowired
    public FlashcardLanguageRepository languageRepository;

    @CommandLine.Parameters(index = "0", description = "Path to the file to import")
    public File file;

    @Override
    public void run() throws RuntimeException {
        try {
            // Read JSON from file
            var fileContents = Files.readString(file.toPath());
            var fileObject = jsonMapper.readValue(fileContents, ImportFlashcardDeckDTO.class);

            // Convert to models
            var flashcardDeck = modelMapper.map(fileObject, FlashcardDeckModel.class);
            var flashcards = modelMapper.map(fileObject.getFlashcards(), FlashcardModel[].class);

            // Populate partial fields
            populateFlashcardDeck(flashcardDeck);
            populateFlashcards(flashcardDeck, flashcards);

            // Save to database
            flashcardDeckRepository.add(null, flashcardDeck);
            for (var flashcard : flashcards)
                flashcardCardRepository.add(flashcard);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateFlashcardDeck(FlashcardDeckModel flashcardDeck) {
        if (flashcardDeck.getId() == null)
            flashcardDeck.setId(UUID.randomUUID().toString());
    }

    private void populateFlashcards(FlashcardDeckModel flashcardDeck, FlashcardModel[] flashcards) {
        var languages = languageRepository.getAll();

        for (var flashcard : flashcards) {
            if (flashcard.getFrontContent() == null)
                throw new RuntimeException("Front content not specified for flashcard");
            if (flashcard.getBackContent() == null)
                throw new RuntimeException("Back content not specified for flashcard");

            if (flashcard.getId() == null)
                flashcard.setId(UUID.randomUUID().toString());

            flashcard.setDeckId(flashcardDeck.getId());
            //flashcard.setLeftLanguage(matchLanguage(languages, flashcard.getLeftLanguage()));
            //flashcard.setRightLanguage(matchLanguage(languages, flashcard.getRightLanguage()));
        }
    }

    private static FlashcardLanguageModel matchLanguage(List<FlashcardLanguageModel> languages, FlashcardLanguageModel partialLanguage) {
        if (partialLanguage.getId() != null) {
            return languages.stream()
                .filter(l -> l.getId().equals(partialLanguage.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Language with id : " + partialLanguage.getId() + " not found"));
        }

        if (partialLanguage.getName() != null) {
            return languages.stream()
                .filter(l -> l.getName().equals(partialLanguage.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Language with name " + partialLanguage.getName() + " not found"));
        }

        if (partialLanguage.getAbbreviation() != null) {
            return languages.stream()
                .filter(l -> l.getAbbreviation().equals(partialLanguage.getAbbreviation()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Language with abbreviation " + partialLanguage.getAbbreviation() + " not found"));
        }

        throw new RuntimeException("Language not specified");
    }
}
