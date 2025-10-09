package com.explik.diybirdyapp.persistence.strategy;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseFeedbackModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputAudioModel;
import com.explik.diybirdyapp.model.exercise.ExerciseInputModel;
import com.explik.diybirdyapp.model.exercise.ExerciseModel;
import com.explik.diybirdyapp.persistence.service.SpeechToTextService;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.AudioContentVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseAnswerVertexFactoryAudio;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component(ExerciseEvaluationTypes.CORRECT_SPEECH_TO_TEXT + ComponentTypes.STRATEGY)
public class ExerciseEvaluationStrategyPronounceFlashcard implements ExerciseEvaluationStrategy {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    ExerciseAnswerVertexFactoryAudio answerVertexFactory;

    @Autowired
    SpeechToTextService speechToTextService;

    @Override
    public ExerciseModel evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext context) {
        if (context == null)
            throw new RuntimeException("Answer model is null");
        if (!(context.getInput() instanceof ExerciseInputAudioModel answerModel))
            throw new RuntimeException("Answer model type is not audio");

        // Transcribe answer
        var audioContentUrl = answerModel.getUrl();
        var audioLangId = "en-US";
        var transcribedText = speechToTextService.generateTranscription(audioContentUrl, audioLangId);
        answerModel.setTranscription(transcribedText);

        // Save answer
        var answerVertex = answerVertexFactory.create(traversalSource, answerModel);

        // Generate feedback
        var exerciseFeedback = ExerciseFeedbackModel.createIndecisiveFeedback();

        var exercise = new ExerciseModel();
        exercise.setId(exerciseVertex.getId());
        exercise.setType(exerciseVertex.getType());
        exercise.setAnswerId(answerVertex.getId());
        exercise.setFeedback(exerciseFeedback);

        return exercise;
    }
}
