package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputRecordAudioDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.service.SpeechToTextService;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAnswerVertexFactoryAudio;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(ExerciseEvaluationTypes.CORRECT_SPEECH_TO_TEXT + ComponentTypes.STRATEGY)
public class ExerciseEvaluationStrategyPronounceFlashcard implements ExerciseEvaluationStrategy {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    ExerciseAnswerVertexFactoryAudio answerVertexFactory;

    @Autowired
    SpeechToTextService speechToTextService;

    @Override
    public ExerciseDto evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext context) {
        if (context == null)
            throw new RuntimeException("Answer model is null");
        if (!(context.getInput()instanceof ExerciseInputRecordAudioDto input))
            throw new RuntimeException("Input model type is not audio");

        // Fetch expected text
        var correctTextVertex = (TextContentVertex)exerciseVertex.getCorrectOptions().getFirst();
        var correctLanguageVertex = correctTextVertex.getLanguage();
        var textToSpeechConfig = ConfigurationVertex.findByLanguageAndType(correctLanguageVertex, ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT).getFirst();
        var textToSpeechId = (String)textToSpeechConfig.getPropertyValue("languageCode");

        // Transcribe answer
        var answerModel = new ExerciseAnswerModel<ExerciseInputRecordAudioDto>();
        answerModel.setExerciseId(context.getExerciseId());
        answerModel.setSessionId(context.getSessionId());
        answerModel.setInput(input);

        var audioContentUrl = input.getUrl();
        var transcribedText = speechToTextService.generateTranscription(audioContentUrl, textToSpeechId);
        answerModel.setProperty("transcription", transcribedText);

        // Save answer
        var answerVertex = answerVertexFactory.create(traversalSource, answerModel);

        var exercise = new ExerciseDto();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getExerciseType().getId());

        // Generate general feedback
        var isCorrect = transcribedText.equalsIgnoreCase(correctTextVertex.getValue());

        if (!transcribedText.isBlank()) {
            // Generate exercise feedback
            var exerciseFeedback = ExerciseFeedbackHelper.createCorrectFeedback(isCorrect);
            exerciseFeedback.setMessage("Answer submitted successfully");

            exercise.setFeedback(exerciseFeedback);

            // Input feedback will be generated below
            var inputFeedback = new ExerciseInputRecordAudioDto.ExerciseInputRecordAudioFeedbackDto();
            inputFeedback.setCorrectValues(List.of(correctTextVertex.getValue()));
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
