package com.explik.diybirdyapp.persistence.operation;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseSessionDto;
import com.explik.diybirdyapp.persistence.command.CreateAudioContentVertexCommand;
import com.explik.diybirdyapp.persistence.command.CreatePronunciationVertexCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.*;
import com.explik.diybirdyapp.service.TextToSpeechService;
import com.explik.diybirdyapp.service.ExerciseCreationService;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component(ExerciseSessionTypes.LEARN_FLASHCARD + ComponentTypes.OPERATIONS)
public class ExerciseSessionOperationsLearnFlashcardDeck implements ExerciseSessionOperations {
    @Autowired
    private CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandHandler;

    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandHandler;

    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    @Autowired
    private ExerciseSessionModelFactory sessionModelFactory;

    @Autowired
    private TextToSpeechService textToSpeechService;

    @Override
    public ExerciseSessionDto init(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var options = context.getSessionModel();

        // Resolve neighboring vertices
        var flashcardDeckVertex = FlashcardDeckVertex.findById(traversalSource, options.getFlashcardDeckId());
        if (flashcardDeckVertex == null)
            throw new IllegalArgumentException("Flashcard deck with id" + options.getFlashcardDeckId() + "not found");
        if (flashcardDeckVertex.getFlashcards().isEmpty())
            throw new IllegalArgumentException("Flashcard deck with id" + options.getFlashcardDeckId() + "is empty");

        // Create/init the session vertex
        var sessionId = options.getId() != null ? options.getId() : java.util.UUID.randomUUID().toString();
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setId(sessionId);
        vertex.setType(ExerciseSessionTypes.LEARN_FLASHCARD);
        vertex.setFlashcardDeck(flashcardDeckVertex);

        var flashcardLanguages = flashcardDeckVertex.getFlashcardLanguages();
        var optionVertex = ExerciseSessionOptionsVertex.create(traversalSource);
        optionVertex.setId(UUID.randomUUID().toString());
        optionVertex.setType(ExerciseSessionTypes.LEARN_FLASHCARD);
        optionVertex.setRetypeCorrectAnswer(false);
        optionVertex.setTextToSpeechEnabled(false);

        for (var languageVertex : flashcardLanguages)
            optionVertex.addAnswerLanguage(languageVertex);

        optionVertex.setIncludeReviewExercises(true);
        optionVertex.setIncludeMultipleChoiceExercises(true);
        optionVertex.setIncludeWritingExercises(true);
        optionVertex.setIncludeListeningExercises(false);
        optionVertex.setIncludePronunciationExercises(true);

        for (var exerciseTypeVertex : getInitialExerciseTypes(traversalSource))
            optionVertex.addExerciseType(exerciseTypeVertex);

        vertex.setOptions(optionVertex);

        // Generate first exercise
        nextExerciseVertex(traversalSource, vertex);
        vertex.reload();

        return sessionModelFactory.create(vertex);
    }

    @Override
    public ExerciseSessionDto nextExercise(GraphTraversalSource traversalSource, ExerciseCreationContext context) {
        var modelId = context.getSessionModel().getId();
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, modelId);
        if (sessionVertex == null)
            throw new RuntimeException("Session with " + modelId +" not found");

        // Generate next exercise
        nextExerciseVertex(traversalSource, sessionVertex);
        sessionVertex.reload();

        return sessionModelFactory.create(sessionVertex);
    }

    // TODO Use flashcard side specified from session options
    private ExerciseVertex nextExerciseVertex(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        FlashcardVertex flashcardVertex;
        var exerciseTypes = sessionVertex.getOptions().getExerciseTypes().stream()
                .map(ExerciseTypeVertex::getId)
                .toList();

        if (exerciseTypes.contains(ExerciseTypes.REVIEW_FLASHCARD)) {
            var reviewExerciseVertex = tryGenerateReviewExercise(traversalSource, sessionVertex);
            if (reviewExerciseVertex != null)
                return reviewExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.SELECT_FLASHCARD)) {
            var selectExerciseVertex = tryGenerateSelectExercise(traversalSource, sessionVertex);
            if (selectExerciseVertex != null)
                return selectExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.LISTEN_AND_SELECT)) {
            var listenAndSelectExerciseVertex = tryGenerateListenAndSelectExercise(traversalSource, sessionVertex);
            if (listenAndSelectExerciseVertex != null)
                return listenAndSelectExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.WRITE_FLASHCARD)) {
            var writeExerciseVertex = tryGenerateWriteExercise(traversalSource, sessionVertex);
            if (writeExerciseVertex != null)
                return writeExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.LISTEN_AND_WRITE)) {
            var listenAndWriteExerciseVertex = tryGenerateListenAndWriteExercise(traversalSource, sessionVertex);
            if (listenAndWriteExerciseVertex != null)
                return listenAndWriteExerciseVertex;
        }

        if (exerciseTypes.contains(ExerciseTypes.PRONOUNCE_FLASHCARD)) {
            var pronounceExerciseVertex = tryGeneratePronounceExercise(traversalSource, sessionVertex);
            if (pronounceExerciseVertex != null)
                return pronounceExerciseVertex;
        }

        // If no flashcards are found, the session is complete
        sessionVertex.setCompleted(true);
        return null;
    }

    private ExerciseVertex tryGenerateReviewExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.REVIEW_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(flashcardVertex));
        
        var command = exerciseCreationService.createExerciseCommand(ExerciseSchemas.REVIEW_FLASHCARD_EXERCISE, exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }

    private ExerciseVertex tryGenerateSelectExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.SELECT_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var flashcardSide = "front";
        var flashcardDeckVertex = sessionVertex.getFlashcardDeck();
        var answerContentType = flashcardVertex.getOtherSide(flashcardSide).getClass();
        FlashcardVertex finalFlashcardVertex = flashcardVertex;
        var alternativeFlashcardVertices = flashcardDeckVertex.getFlashcards().stream()
                .filter(flashcard -> !flashcard.getId().equals(finalFlashcardVertex.getId())) // Skips the current flashcard
                .filter(flashcard -> flashcard.getOtherSide(flashcardSide).getClass() == answerContentType) // Skips flashcards with different content type
                .limit(3)
                .collect(Collectors.toList());

        var correctContentVertex = flashcardVertex.getOtherSide(flashcardSide);
        var incorrectContentVertices = alternativeFlashcardVertices
                .stream()
                .map(f -> f.getOtherSide(flashcardSide))
                .toList();

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withFlashcardContent(flashcardVertex, flashcardSide))
                .withSelectOptionsInput(new ExerciseInputParametersSelectOptions()
                        .withCorrectOptions(List.of(correctContentVertex))
                        .withIncorrectOptions(incorrectContentVertices)
                );
        
        var command = exerciseCreationService.createExerciseCommand(ExerciseSchemas.SELECT_FLASHCARD_EXERCISE, exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }

    private ExerciseVertex tryGenerateListenAndSelectExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.LISTEN_AND_SELECT);
        if (flashcardVertex == null)
            return null;

        // Fetch content
        var flashcardSide = "front";
        var correctContentVertex = flashcardVertex.getSide(flashcardSide);
        if (!(correctContentVertex instanceof TextContentVertex textContentVertex))
            return null;

        var pronunciationAudioVertex = tryFetchOrGeneratePronunciation(traversalSource, textContentVertex);
        if (pronunciationAudioVertex == null)
            return null;

        // Fetch alternative answers
        var flashcardDeckVertex = sessionVertex.getFlashcardDeck();
        var alternativeFlashcardVertices = flashcardDeckVertex.getFlashcards().stream()
                .filter(flashcard -> !flashcard.getId().equals(flashcardVertex.getId())) // Skips the current flashcard
                .filter(flashcard -> flashcard.getSide(flashcardSide).getClass() == correctContentVertex.getClass()) // Skips flashcards with different content type
                .limit(3)
                .collect(Collectors.toList());
        var incorrectContentVertices = alternativeFlashcardVertices
                .stream()
                .map(f -> f.getSide(flashcardSide))
                .toList();

        // Create exercise
        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(pronunciationAudioVertex))
                .withSelectOptionsInput(new ExerciseInputParametersSelectOptions()
                        .withCorrectOptions(List.of(correctContentVertex))
                        .withIncorrectOptions(incorrectContentVertices)
                );
        
        var command = exerciseCreationService.createExerciseCommand(ExerciseSchemas.LISTEN_AND_SELECT_EXERCISE, exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }

    private ExerciseVertex tryGenerateWriteExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.WRITE_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var flashcardSide = "front";
        var questionContentVertex = flashcardVertex.getSide(flashcardSide);
        var answerContentVertex = flashcardVertex.getOtherSide(flashcardSide);

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withFlashcardContent(flashcardVertex, flashcardSide))
                .withWriteTextInput(new ExerciseInputParametersWriteText().withCorrectOption(answerContentVertex));
        
        var command = exerciseCreationService.createExerciseCommand(ExerciseSchemas.WRITE_FLASHCARD_EXERCISE, exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }

    private ExerciseVertex tryGenerateListenAndWriteExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.LISTEN_AND_WRITE);
        if (flashcardVertex == null)
            return null;

        var flashcardSide = "front";
        var questionContentVertex = flashcardVertex.getSide(flashcardSide);
        if (!(questionContentVertex instanceof TextContentVertex textContentVertex))
            return null;

        var pronunciationAudioVertex = tryFetchOrGeneratePronunciation(traversalSource, textContentVertex);
        if (pronunciationAudioVertex == null)
            return null;

        var answerContentVertex = flashcardVertex.getOtherSide(flashcardSide);
        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withContent(pronunciationAudioVertex))
                .withWriteTextInput(new ExerciseInputParametersWriteText().withCorrectOption(answerContentVertex));
        
        var command = exerciseCreationService.createExerciseCommand(ExerciseSchemas.LISTEN_AND_WRITE_EXERCISE, exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }

    private ExerciseVertex tryGeneratePronounceExercise(GraphTraversalSource traversalSource, ExerciseSessionVertex sessionVertex) {
        var flashcardVertex = FlashcardVertex.findFirstNonExercised(traversalSource, sessionVertex.getId(), ExerciseTypes.PRONOUNCE_FLASHCARD);
        if (flashcardVertex == null)
            return null;

        var flashcardSide = "front";
        var flashcardContentVertex = flashcardVertex.getSide(flashcardSide);
        if (!(flashcardContentVertex instanceof TextContentVertex textContentVertex))
            return null;

        var exerciseParameters = new ExerciseParameters()
                .withSession(sessionVertex)
                .withContent(new ExerciseContentParameters().withFlashcardContent(flashcardVertex, flashcardSide))
                .withRecordAudioInput(new ExerciseInputParametersRecordAudio().withCorrectOption(textContentVertex));
        
        var command = exerciseCreationService.createExerciseCommand(ExerciseSchemas.PRONOUNCE_FLASHCARD_EXERCISE, exerciseParameters);
        createExerciseCommandHandler.handle(command);
        
        var exerciseId = exerciseParameters.getId() != null ? exerciseParameters.getId() : command.getId();
        return ExerciseVertex.getById(traversalSource, exerciseId);
    }

    private AudioContentVertex tryFetchOrGeneratePronunciation(GraphTraversalSource traversalSource, TextContentVertex textContentVertex) {
        var existingPronunciation = textContentVertex.getMainPronunciation();
        if (existingPronunciation != null)
            return existingPronunciation.getAudioContent();

        // Generate pronunciation file
        var voiceConfig = generateVoiceConfig(textContentVertex);
        if (voiceConfig == null)
            return null;

        var filePath = textContentVertex.getId() + ".wav";
        try {
            textToSpeechService.generateAudioFile(voiceConfig, filePath);
        }
        catch (Exception e) {
            System.err.println("Failed to generate audio for text content: " + textContentVertex.getId());
            System.err.println(e.toString());
            return null;
        }

        // Save pronunciation to graph
        var createCommand = new CreatePronunciationVertexCommand();
        createCommand.setId(UUID.randomUUID().toString());
        createCommand.setAudioUrl(filePath);
        createCommand.setSourceVertex(textContentVertex.getId());
        createPronunciationVertexCommandHandler.handle(createCommand);

        return null;
    }

    private TextToSpeechService.Text generateVoiceConfig(TextContentVertex textContentVertex) {
        var languageVertex = textContentVertex.getLanguage();

        var textToSpeechConfigs = ConfigurationVertex.findByLanguageAndType(languageVertex, ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        if (textToSpeechConfigs.isEmpty())
            return null;

        var textToSpeechConfig = textToSpeechConfigs.getFirst();
        return new TextToSpeechService.Text(
                textContentVertex.getValue(),
                textToSpeechConfig.getPropertyValue("languageCode"),
                textToSpeechConfig.getPropertyValue("voiceName"),
                "LINEAR16"
        );
    }

    private List<ExerciseTypeVertex> getInitialExerciseTypes(GraphTraversalSource traversalSource) {
        // This list should not contain any non-flashcard-based exercises as it breaks the next-exercise algorithm
        return List.of(
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.REVIEW_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.SELECT_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.WRITE_FLASHCARD),
                ExerciseTypeVertex.findById(traversalSource, ExerciseTypes.PRONOUNCE_FLASHCARD)
        );
    }
}
