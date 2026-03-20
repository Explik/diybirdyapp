package com.explik.diybirdyapp.manager.exerciseSessionManager.LearnFlashcardDeck;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.manager.contentCrawler.FlashcardDeckContentCrawler;
import com.explik.diybirdyapp.manager.exerciseCreationManager.*;
import com.explik.diybirdyapp.manager.exerciseCreationManager.MultiStageTapPairsExerciseCreationManager;
import com.explik.diybirdyapp.persistence.vertex.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates individual exercises for a specific content vertex and exercise type.
 * Session-level orchestration (batching, round traversal, and content selection) lives in the
 * corresponding exercise session manager.
 */
@Component
public class FlashcardDeckExerciseManager {
    
    @Autowired
    private ReviewFlashcardExerciseCreationManager reviewFlashcardExerciseCreationManager;

    @Autowired
    private ViewFlashcardExerciseCreationManager viewFlashcardExerciseCreationManager;

    @Autowired
    private SelectFlashcardExerciseCreationManager selectFlashcardExerciseCreationManager;

    @Autowired
    private WriteFlashcardExerciseCreationManager writeFlashcardExerciseCreationManager;

    @Autowired
    private ListenAndSelectExerciseCreationManager listenAndSelectExerciseCreationManager;

    @Autowired
    private ListenAndWriteExerciseCreationManager listenAndWriteExerciseCreationManager;

    @Autowired
    private PronounceFlashcardExerciseCreationManager pronounceFlashcardExerciseCreationManager;

    @Autowired
    private MultiStageTapPairsExerciseCreationManager multiStageTapPairsExerciseCreationManager;

    @Autowired
    private FlashcardDeckContentCrawler deckContentCrawler;

    public ExerciseVertex createExerciseForContent(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            ExerciseSessionStateVertex stateVertex,
            AbstractVertex content) {

        if (sessionVertex == null || content == null) {
            return null;
        }

        var exerciseTypes = determineExerciseTypesForContent(sessionVertex, content);
        if (exerciseTypes.isEmpty()) {
            return null;
        }

        int startIndex = 0;
        var contentId = getContentId(content);
        if (stateVertex != null && contentId != null) {
            var lastExerciseType = stateVertex.getLastExerciseTypeForContent(contentId);
            int lastExerciseTypeIndex = exerciseTypes.indexOf(lastExerciseType);
            if (lastExerciseTypeIndex >= 0) {
                startIndex = (lastExerciseTypeIndex + 1) % exerciseTypes.size();
            }
        }

        for (int i = 0; i < exerciseTypes.size(); i++) {
            int exerciseTypeIndex = (startIndex + i) % exerciseTypes.size();
            var exerciseType = exerciseTypes.get(exerciseTypeIndex);
            var exercise = tryCreateExerciseForType(
                    traversalSource,
                    sessionVertex,
                    content,
                    exerciseType);

            if (exercise != null) {
                if (stateVertex != null && contentId != null) {
                    stateVertex.setLastExerciseTypeForContent(contentId, exerciseType);
                }

                return exercise;
            }
        }

        return null;
    }

    private ExerciseVertex tryCreateExerciseForType(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            AbstractVertex content,
            String exerciseType) {

        FlashcardVertex flashcardVertex = null;
        PronunciationVertex pronunciationVertex = null;
        TextContentVertex textContentVertex = null;

        if (content instanceof FlashcardVertex) {
            flashcardVertex = (FlashcardVertex) content;
        } else if (content instanceof PronunciationVertex) {
            pronunciationVertex = (PronunciationVertex) content;
        } else if (content instanceof TextContentVertex) {
            textContentVertex = (TextContentVertex) content;
        } else {
            return null;
        }

        return switch (exerciseType) {
            case ExerciseTypes.REVIEW_FLASHCARD ->
                    flashcardVertex != null
                            ? tryCreateReviewExercise(traversalSource, sessionVertex, flashcardVertex)
                            : null;
            case ExerciseTypes.VIEW_FLASHCARD ->
                    flashcardVertex != null
                            ? tryCreateViewExercise(traversalSource, sessionVertex, flashcardVertex)
                            : null;
            case ExerciseTypes.SELECT_FLASHCARD ->
                    flashcardVertex != null
                            ? tryCreateSelectExercise(traversalSource, sessionVertex, flashcardVertex)
                            : null;
            case ExerciseTypes.LISTEN_AND_SELECT ->
                    pronunciationVertex != null
                            ? tryCreateListenAndSelectExercise(traversalSource, sessionVertex, pronunciationVertex)
                            : null;
            case ExerciseTypes.WRITE_FLASHCARD ->
                    flashcardVertex != null
                            ? tryCreateWriteExercise(traversalSource, sessionVertex, flashcardVertex)
                            : null;
            case ExerciseTypes.LISTEN_AND_WRITE ->
                    pronunciationVertex != null
                            ? tryCreateListenAndWriteExercise(traversalSource, sessionVertex, pronunciationVertex)
                            : null;
            case ExerciseTypes.PRONOUNCE_FLASHCARD ->
                    textContentVertex != null
                            ? tryCreatePronounceExercise(traversalSource, sessionVertex, textContentVertex)
                            : null;
            default -> null;
        };
    }

    private List<String> determineExerciseTypesForContent(
            ExerciseSessionVertex sessionVertex,
            AbstractVertex content) {
        var options = sessionVertex.getOptions();
        if (options == null) {
            return List.of();
        }

        var exerciseTypes = new ArrayList<String>();

        if (content instanceof FlashcardVertex) {
            if (options.getIncludeReviewExercises()) {
                exerciseTypes.add(ExerciseTypes.VIEW_FLASHCARD);
            }

            if (options.getIncludeMultipleChoiceExercises()) {
                exerciseTypes.add(ExerciseTypes.SELECT_FLASHCARD);
            }

            if (options.getIncludeWritingExercises()) {
                exerciseTypes.add(ExerciseTypes.WRITE_FLASHCARD);
            }
        } else if (content instanceof PronunciationVertex) {
            if (options.getIncludeMultipleChoiceExercises() && options.getIncludeListeningExercises()) {
                exerciseTypes.add(ExerciseTypes.LISTEN_AND_SELECT);
            }

            if (options.getIncludeWritingExercises() && options.getIncludeListeningExercises()) {
                exerciseTypes.add(ExerciseTypes.LISTEN_AND_WRITE);
            }
        } else if (content instanceof TextContentVertex) {
            if (options.getIncludePronunciationExercises()) {
                exerciseTypes.add(ExerciseTypes.PRONOUNCE_FLASHCARD);
            }
        }

        return exerciseTypes;
    }

    private String getContentId(AbstractVertex vertex) {
        if (vertex instanceof ContentVertex contentVertex) {
            return contentVertex.getId();
        }

        if (vertex instanceof PronunciationVertex pronunciationVertex) {
            return pronunciationVertex.getId();
        }

        if (vertex instanceof FlashcardVertex flashcardVertex) {
            return flashcardVertex.getId();
        }

        return null;
    }

    public ExerciseVertex createMultiStageTapPairsExercise(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex) {
        var context = ExerciseCreationContext.createForSession(
                sessionVertex,
                ExerciseTypes.MULTI_STAGE_TAP_PAIRS);
        setContentStreamFromFlashcardDeck(context, sessionVertex);
        return multiStageTapPairsExerciseCreationManager.createExercise(traversalSource, context);
    }

    private void setContentStreamFromFlashcardDeck(
            ExerciseCreationContext context,
            ExerciseSessionVertex sessionVertex) {
        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck != null) {
            context.setContentStream(deckContentCrawler.crawl(flashcardDeck));
        }
    }

    private ExerciseVertex tryCreateReviewExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        String flashcardSide = null;
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null) {
            // Determine which side of the flashcard matches the target language
            if (flashcardVertex.getLeftContent() instanceof TextContentVertex leftTextContent &&
                leftTextContent.getLanguage().getId().equals(targetLanguage.getId())) {
                flashcardSide = "front";
            }
            else if (flashcardVertex.getRightContent() instanceof TextContentVertex rightTextContent &&
                     rightTextContent.getLanguage().getId().equals(targetLanguage.getId())) {
                flashcardSide = "back";
            }
        }

        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                flashcardSide,
                ExerciseTypes.REVIEW_FLASHCARD);
        
        return reviewFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateViewExercise(
            GraphTraversalSource traversalSource,
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                null,
                ExerciseTypes.VIEW_FLASHCARD);

        return viewFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateSelectExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.SELECT_FLASHCARD);
        setContentStreamFromFlashcardDeck(context, sessionVertex);
        
        return selectFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateListenAndSelectExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            PronunciationVertex pronunciationVertex) {
        // Skip if pronunciation language doesn't match session target language
        var currentLanguageId = pronunciationVertex.getAudioContent().getLanguage().getId();
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null && !currentLanguageId.equals(targetLanguage.getId())) {
            return null;
        }
        
        var context = ExerciseCreationContext.createForPronunciation(
                sessionVertex,
                pronunciationVertex,
                ExerciseTypes.LISTEN_AND_SELECT);
        setContentStreamFromFlashcardDeck(context, sessionVertex);
        
        return listenAndSelectExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateWriteExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            FlashcardVertex flashcardVertex) {
        var context = ExerciseCreationContext.createForFlashcard(
                sessionVertex,
                flashcardVertex,
                "front",
                ExerciseTypes.WRITE_FLASHCARD);
        
        return writeFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreateListenAndWriteExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            PronunciationVertex pronunciationVertex) {
        // Skip if pronunciation language doesn't match session target language
        var currentLanguageId = pronunciationVertex.getAudioContent().getLanguage().getId();
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null && !currentLanguageId.equals(targetLanguage.getId())) {
            return null;
        }
        
        var context = ExerciseCreationContext.createForPronunciation(
                sessionVertex,
                pronunciationVertex,
                ExerciseTypes.LISTEN_AND_WRITE);
        
        return listenAndWriteExerciseCreationManager.createExercise(traversalSource, context);
    }

    private ExerciseVertex tryCreatePronounceExercise(
            GraphTraversalSource traversalSource, 
            ExerciseSessionVertex sessionVertex,
            TextContentVertex textContentVertex) {
        // Skip if text language doesn't match session target language
        var currentLanguageId = textContentVertex.getLanguage().getId();
        var targetLanguage = sessionVertex.getOptions().getTargetLanguage();
        if (targetLanguage != null && !currentLanguageId.equals(targetLanguage.getId())) {
            return null;
        }

        var context = ExerciseCreationContext.createForText(
                sessionVertex,
                textContentVertex,
                ExerciseTypes.PRONOUNCE_FLASHCARD);
        
        return pronounceFlashcardExerciseCreationManager.createExercise(traversalSource, context);
    }
}
