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
        var algorithm = vertex.getOptions() != null ? vertex.getOptions().getAlgorithm() : null;

        if ("SimpleSort".equals(algorithm)) {
            return createSimpleSortProgress(vertex);
        } else {
            return createSuperMemo2Progress(vertex);
        }
    }

    // -------------------------------------------------------------------------
    // SimpleSort: know / dont-know / unsorted
    // -------------------------------------------------------------------------

    private ExerciseSessionProgressDto createSimpleSortProgress(ExerciseSessionVertex vertex) {
        var progressModel = new ExerciseSessionProgressDto();
        progressModel.setType("review-progress");

        var flashcardDeck = vertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            setZeroProgress(progressModel);
            return progressModel;
        }

        var flashcards = flashcardDeck.getFlashcards();
        int totalFlashcards = flashcards.size();
        if (totalFlashcards == 0) {
            setZeroProgress(progressModel);
            return progressModel;
        }

        // Build a map from flashcard ID → pile ("know" | "dont-know")
        Map<String, String> pileByFlashcardId = new HashMap<>();
        for (var state : vertex.getStatesWithType("SimpleSort")) {
            var content = state.getContent();
            if (content == null) continue;
            String pile = state.getPropertyValue("pile", null);
            if (pile == null) continue;
            pileByFlashcardId.put(content.getId(), pile);
        }

        int unsortedCount = 0;
        int dontKnowCount = 0;
        int knowCount = 0;

        for (var flashcard : flashcards) {
            var pile = pileByFlashcardId.get(flashcard.getId());
            if (pile == null) {
                unsortedCount++;
            } else if ("dont-know".equals(pile)) {
                dontKnowCount++;
            } else {
                knowCount++;
            }
        }

        // Map to existing DTO fields: new=unsorted, learning=dont-know, long-term=know
        progressModel.setReviewNewPercentage((int) Math.round((double) unsortedCount / totalFlashcards * 100));
        progressModel.setReviewLearningPercentage((int) Math.round((double) dontKnowCount / totalFlashcards * 100));
        progressModel.setReviewLongTermPercentage((int) Math.round((double) knowCount / totalFlashcards * 100));

        return progressModel;
    }

    // -------------------------------------------------------------------------
    // SuperMemo 2: new / learning / long-term
    // -------------------------------------------------------------------------

    private ExerciseSessionProgressDto createSuperMemo2Progress(ExerciseSessionVertex vertex) {
        var progressModel = new ExerciseSessionProgressDto();
        progressModel.setType("review-progress");

        var flashcardDeck = vertex.getFlashcardDeck();
        if (flashcardDeck == null) {
            setZeroProgress(progressModel);
            return progressModel;
        }

        var flashcards = flashcardDeck.getFlashcards();
        int totalFlashcards = flashcards.size();
        if (totalFlashcards == 0) {
            setZeroProgress(progressModel);
            return progressModel;
        }

        var now = System.currentTimeMillis();
        var longTermThreshold = now + ONE_DAY_MS;

        Map<String, Long> nextShowByFlashcardId = new HashMap<>();
        for (var state : vertex.getStatesWithType("SuperMemo2")) {
            var content = state.getContent();
            if (content == null) continue;
            Long nextShow = state.getPropertyValue("next_show", null);
            if (nextShow == null) continue;
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

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void setZeroProgress(ExerciseSessionProgressDto progressModel) {
        progressModel.setReviewLongTermPercentage(0);
        progressModel.setReviewLearningPercentage(0);
        progressModel.setReviewNewPercentage(0);
    }
}
