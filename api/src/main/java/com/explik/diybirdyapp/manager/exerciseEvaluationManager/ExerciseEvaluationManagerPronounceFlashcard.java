package com.explik.diybirdyapp.manager.exerciseEvaluationManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputRecordAudioDto;
import com.explik.diybirdyapp.persistence.command.CreateExerciseAnswerAudioCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.query.GetCorrectExerciseAnswerSpeakModelForExerciseQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
import com.explik.diybirdyapp.persistence.query.modelFactory.CorrectExerciseAnswerSpeakModel;
import com.explik.diybirdyapp.service.storageService.SpeechToTextService;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ExerciseEvaluationTypes.CORRECT_SPEECH_TO_TEXT + ComponentTypes.STRATEGY)
public class ExerciseEvaluationManagerPronounceFlashcard implements ExerciseEvaluationManager {
    @Autowired
    CommandHandler<CreateExerciseAnswerAudioCommand> createExerciseAnswerAudioCommandHandler;

    @Autowired
    SpeechToTextService speechToTextService;

    @Autowired
    QueryHandler<GetCorrectExerciseAnswerSpeakModelForExerciseQuery, CorrectExerciseAnswerSpeakModel> commandHandler;

    @Override
    public ExerciseDto evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext context) {
        if (context == null)
            throw new RuntimeException("Answer model is null");
        if (!(context.getInput()instanceof ExerciseInputRecordAudioDto input))
            throw new RuntimeException("Input model type is not audio");

        // Fetch speech-to-text configuration and correct text values using query
        var query = new GetCorrectExerciseAnswerSpeakModelForExerciseQuery();
        query.setExerciseId(exerciseVertex.getId());
        
        var config = commandHandler.handle(query);
        var textToSpeechId = config.getLanguageCode();
        var correctTextValues = config.getCorrectTextValues();

        // Transcribe answer
        var audioContentUrl = input.getUrl();
        var transcribedText = speechToTextService.generateTranscription(audioContentUrl, textToSpeechId);

        // Save answer
        var command = new CreateExerciseAnswerAudioCommand();
        command.setExerciseId(context.getExerciseId());
        command.setSessionId(context.getSessionId());
        command.setAudioUrl(audioContentUrl);
        createExerciseAnswerAudioCommandHandler.handle(command);

        var exercise = new ExerciseDto();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getExerciseType().getId());

        // Generate general feedback (check if transcribed text matches any correct option)
        var isCorrect = correctTextValues.stream()
                .anyMatch(correctValue -> correctValue.equalsIgnoreCase(transcribedText));

        if (!transcribedText.isBlank()) {
            // Generate exercise feedback
            var exerciseFeedback = ExerciseFeedbackHelper.createCorrectFeedback(isCorrect);
            exerciseFeedback.setMessage("Answer submitted successfully");

            exercise.setFeedback(exerciseFeedback);

            // Input feedback will be generated below
            var inputFeedback = new ExerciseInputRecordAudioDto.ExerciseInputRecordAudioFeedbackDto();
            inputFeedback.setCorrectValues(correctTextValues);
            inputFeedback.setIncorrectValues(isCorrect ? List.of() : List.of(transcribedText));

            var exerciseInput = new ExerciseInputRecordAudioDto();
            exerciseInput.setFeedback(inputFeedback);

            exercise.setInput(exerciseInput);
        }
        else {
            var exerciseFeedback = ExerciseFeedbackHelper.createIndecisiveFeedback();
            exerciseFeedback.setMessage("Could not transcribe audio. Please try again.");

            exercise.setFeedback(exerciseFeedback);
        }

        return exercise;
    }
}
