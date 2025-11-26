package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputRecordAudioDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.service.SpeechToTextService;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAnswerVertexFactoryAudio;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

        // Transcribe answer
        var answerModel = new ExerciseAnswerModel<ExerciseInputRecordAudioDto>();
        answerModel.setExerciseId(context.getExerciseId());
        answerModel.setSessionId(context.getSessionId());
        answerModel.setInput(input);

        var audioContentUrl = input.getUrl();
        var audioLangId = "en-US";
        var transcribedText = speechToTextService.generateTranscription(audioContentUrl, audioLangId);
        answerModel.setProperty("transcription", transcribedText);

        // Save answer
        var answerVertex = answerVertexFactory.create(traversalSource, answerModel);

        // Generate feedback
        var exerciseFeedback = ExerciseFeedbackHelper.createIndecisiveFeedback();

        var exercise = new ExerciseDto();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getExerciseType().getId());
        exercise.setFeedback(exerciseFeedback);

        return exercise;
    }
}
