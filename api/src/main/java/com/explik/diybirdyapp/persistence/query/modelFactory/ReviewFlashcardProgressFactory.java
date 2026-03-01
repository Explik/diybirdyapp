package com.explik.diybirdyapp.persistence.query.modelFactory;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionProgressDto;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReviewFlashcardProgressFactory implements ExerciseSessionProgressFactory {

    private static final long ONE_DAY_MS = 86_400_000L;

    @Override
    public ExerciseSessionProgressDto createProgress(ExerciseSessionVertex vertex) {
        var progressModel = new ExerciseSessionProgressDto();
        progressModel.setType("review-progress");

        var flashcardDeck = vertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            progressModel.setReviewLongTermPercentage(0);
            progressModel.setReviewLearningPercentage(0);
            progressModel.setReviewNewPercentage(0);
            return progressModel;
        }

        var flashcards = flashcardDeck.getFlashcards();
        int totalFlashcards = flashcards.size();
        if (totalFlashcards == 0) {
            progressModel.setReviewLongTermPercentage(0);
            progressModel.setReviewLearningPercentage(0);
            progressModel.setReviewNewPercentage(0);
            return progressModel;
        }

        var now = System.currentTimeMillis();
        var longTermThreshold = now + ONE_DAY_MS;

        Map<String, Long> nextShowByFlashcardId = new HashMap<>();
        for (var state : vertex.getStatesWithType("SuperMemo2")) {
            var content = state.getContent();
            if (content == null) {
                continue;
            }

            Long nextShow = state.getPropertyValue("next_show", null);
            if (nextShow == null) {
                continue;
            }

            nextShowByFlashcardId.put(content.getId(), nextShow);
        }

        int newCount = 0;
        int learningCount = 0;
        int longTermCount = 0;

        for (var flashcard : flashcards) {
            var nextShow = nextShowByFlashcardId.get(flashcard.getId());
            if (nextShow == null) {
                newCount++;
                continue;
            }

            if (nextShow > longTermThreshold) {
                longTermCount++;
            } else {
                learningCount++;
            }
        }

        progressModel.setReviewLongTermPercentage((int) Math.round((double) longTermCount / totalFlashcards * 100));
        progressModel.setReviewLearningPercentage((int) Math.round((double) learningCount / totalFlashcards * 100));
        progressModel.setReviewNewPercentage((int) Math.round((double) newCount / totalFlashcards * 100));

        return progressModel;
    }
}