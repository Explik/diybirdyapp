package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.ContentTypes;
import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.persistence.command.*;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchema;
import com.explik.diybirdyapp.persistence.vertex.FlashcardVertex;
import com.explik.diybirdyapp.persistence.schema.parameter.ExerciseParameters;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for creating exercises using the command pattern.
 * This service translates exercise parameters and schema into domain-level commands
 * that represent complete business operations.
 */
@Service
public class ExerciseCreationService {

    /**
     * Creates a domain-level command for creating an exercise based on the schema and parameters.
     * 
     * @param schema The exercise schema defining the structure
     * @param options The parameters for creating the exercise
     * @return A domain command representing the complete exercise creation operation
     */
    public CreateExerciseCommand createExerciseCommand(ExerciseSchema schema, ExerciseParameters options) {
        var command = new CreateExerciseCommand();

        // Set exercise ID
        var exerciseId = options.getId() != null ? options.getId() : UUID.randomUUID().toString();
        command.setId(exerciseId);

        // Set exercise type
        command.setExerciseTypeId(schema.getExerciseType());

        // Set session if provided
        if (options.getSession() != null) {
            command.setSessionId(options.getSession().getId());
        }

        // Set target language if required
        if (schema.getRequireTargetLanguage() && options.getTargetLanguage() != null) {
            command.setTargetLanguageId(options.getTargetLanguage());
        }

        // Set content
        if (schema.getContentType() != null) {
            setContent(command, schema, options);
        }

        // Set input
        if (schema.getInputType() != null) {
            setInput(command, schema, options);
        }

        return command;
    }

    private void setContent(CreateExerciseCommand command, ExerciseSchema schema, ExerciseParameters options) {
        String contentType = schema.getContentType();

        if (contentType.equals(ContentTypes.FLASHCARD_SIDE)) {
            command.setFlashcardId(((FlashcardVertex)options.getContent().getVertex()).getId());
            command.setFlashcardSide(options.getContent().getFlashcardSide());
        }
        else if (contentType.equals(ContentTypes.FLASHCARD) ||
                 contentType.equals(ContentTypes.AUDIO) || 
                 contentType.equals(ContentTypes.IMAGE) ||
                 contentType.equals(ContentTypes.TEXT) ||
                 contentType.equals(ContentTypes.VIDEO)) {
            command.setContentId(options.getContent().getVertex().getId());
        }
        else {
            throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }

    private void setInput(CreateExerciseCommand command, ExerciseSchema schema, ExerciseParameters options) {
        String inputType = schema.getInputType();

        if (inputType.equals(ExerciseInputTypes.ARRANGE_TEXT_OPTIONS)) {
            var inputOptions = options.getArrangeTextOptionsInput();
            if (inputOptions == null)
                throw new IllegalArgumentException("ArrangeTextOptionsInput is required for input type: " + inputType);

            var inputCommand = new CreateArrangeTextOptionsInputCommand();
            inputCommand.setOptionIds(
                inputOptions.getOptions().stream()
                    .map(vertex -> vertex.getId())
                    .collect(Collectors.toList())
            );
            command.setArrangeTextOptionsInput(inputCommand);
        }
        else if (inputType.equals(ExerciseInputTypes.SELECT_OPTIONS)) {
            var inputOptions = options.getSelectOptionsInput();
            if (inputOptions == null)
                throw new IllegalArgumentException("SelectOptionsInput is required for input type: " + inputType);

            var inputCommand = new CreateSelectOptionsInputCommand();
            inputCommand.setCorrectOptionIds(
                inputOptions.getCorrectOptions().stream()
                    .map(vertex -> vertex.getId())
                    .collect(Collectors.toList())
            );
            inputCommand.setIncorrectOptionIds(
                inputOptions.getIncorrectOptions().stream()
                    .map(vertex -> vertex.getId())
                    .collect(Collectors.toList())
            );
            command.setSelectOptionsInput(inputCommand);
        }
        else if (inputType.equals(ExerciseInputTypes.PAIR_OPTIONS)) {
            var inputOptions = options.getPairOptionsInput();
            if (inputOptions == null)
                throw new IllegalArgumentException("PairOptionsInput is required for input type: " + inputType);

            var inputCommand = new CreatePairOptionsInputCommand();
            inputCommand.setPairs(
                inputOptions.getPairs().stream()
                    .map(pair -> new CreatePairOptionsInputCommand.PairDefinition(
                        pair.get(0).getId(),
                        pair.get(1).getId()
                    ))
                    .collect(Collectors.toList())
            );
            command.setPairOptionsInput(inputCommand);
        }
        else if (inputType.equals(ExerciseInputTypes.WRITE_TEXT)) {
            var inputText = options.getWriteTextInput();
            if (inputText != null) {
                var inputCommand = new CreateWriteTextInputCommand();
                inputCommand.setCorrectOptionId(inputText.getCorrectOption().getId());
                command.setWriteTextInput(inputCommand);
            }
        }
        else if (inputType.equals(ExerciseInputTypes.RECORD_AUDIO)) {
            var inputAudio = options.getRecordAudioInput();
            if (inputAudio != null) {
                var inputCommand = new CreateRecordAudioInputCommand();
                inputCommand.setCorrectOptionId(inputAudio.getCorrectOption().getId());
                command.setRecordAudioInput(inputCommand);
            }
        }
        else if (inputType.equals(ExerciseInputTypes.RECOGNIZABILITY_RATING)) {
            command.setRecognizabilityRatingInput(new CreateRecognizabilityRatingInputCommand());
        }
        else {
            throw new IllegalArgumentException("Unsupported input type: " + inputType);
        }
    }
}
