package com.explik.diybirdyapp.manager.exerciseEvaluationManager;

import com.explik.diybirdyapp.ComponentTypes;
import com.explik.diybirdyapp.ExerciseEvaluationTypes;
import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultiStagePairOptionsDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultiStagePairOptionsDto.FeedbackPair;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultiStagePairOptionsDto.MultiStagePairOptionsFeedback;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultiStagePairOptionsDto.PairOptionsInputOptionDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultiStagePairOptionsDto.PairOptionsInputTextOptionDto;
import com.explik.diybirdyapp.model.exercise.ExerciseInputMultiStagePairOptionsDto.SelectedPair;
import com.explik.diybirdyapp.persistence.command.CreateExerciseFeedbackCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.ExerciseAnswerVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import com.explik.diybirdyapp.persistence.vertex.PairVertex;
import com.explik.diybirdyapp.persistence.vertex.TextContentVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Evaluates a single-pair selection in the multi-stage tap-pairs exercise.
 *
 * <p>On a <b>correct</b> pair:
 * <ul>
 *   <li>The matched pair is removed from the visible option columns.</li>
 *   <li>A new, previously unseen pair is inserted at a random position.</li>
 *   <li>{@code answeredCount} is incremented.</li>
 *   <li>When {@code answeredCount == maxPairs} the exercise is considered complete
 *       (the response feedback state is {@code "correct"}, which the frontend uses
 *       to trigger the next-exercise flow).</li>
 * </ul>
 *
 * <p>On an <b>incorrect</b> pair:
 * <ul>
 *   <li>The visible columns are returned unchanged.</li>
 *   <li>{@code answeredCount} is not incremented.</li>
 * </ul>
 */
@Component(ExerciseEvaluationTypes.MULTI_STAGE_CORRECT_PAIRS + ComponentTypes.STRATEGY)
public class ExerciseEvaluationManagerMultiStagePairOptions implements ExerciseEvaluationManager {

    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private CommandHandler<CreateExerciseFeedbackCommand> createExerciseFeedbackCommandHandler;

    @Override
    public ExerciseDto evaluate(ExerciseVertex exerciseVertex, ExerciseEvaluationContext context) {
        if (context == null)
            throw new RuntimeException("Evaluation context is null");
        if (!(context.getInput() instanceof ExerciseInputMultiStagePairOptionsDto answerDto))
            throw new RuntimeException("Expected ExerciseInputMultiStagePairOptionsDto but got: "
                    + (context.getInput() == null ? "null" : context.getInput().getClass().getName()));

        var selectedPair = answerDto.getSelectedPair();
        if (selectedPair == null)
            throw new RuntimeException("selectedPair is required");

        // --- Load all pairs from the exercise ---
        @SuppressWarnings("unchecked")
        List<PairVertex> allPairs = (List<PairVertex>) exerciseVertex.getOptionPairs();

        // --- Check if the selected pair is correct ---
        boolean isCorrect = allPairs.stream().anyMatch(p ->
                p.getLeftContent().getId().equals(selectedPair.getLeftId())
                        && p.getRightContent().getId().equals(selectedPair.getRightId()));

        // --- Save answer vertex ---
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, answerDto.getSessionId());
        var answerId = UUID.randomUUID().toString();
        var answerVertex = ExerciseAnswerVertex.create(traversalSource);
        answerVertex.setId(answerId);
        answerVertex.setExercise(exerciseVertex);
        if (sessionVertex != null) answerVertex.setSession(sessionVertex);

        // --- Persist feedback ---
        var feedbackCommand = new CreateExerciseFeedbackCommand();
        feedbackCommand.setExerciseAnswerId(answerId);
        feedbackCommand.setType("general");
        feedbackCommand.setStatus(isCorrect ? "correct" : "incorrect");
        createExerciseFeedbackCommandHandler.handle(feedbackCommand);

        // --- Load server-side state (not from the client DTO) ---
        ExerciseInputMultiStagePairOptionsDto stateDto = loadServerState(exerciseVertex, allPairs);

        // --- Build updated input DTO ---
        ExerciseInputMultiStagePairOptionsDto updatedInput;
        if (isCorrect) {
            updatedInput = buildCorrectResult(stateDto, allPairs, selectedPair);
        } else {
            updatedInput = buildIncorrectResult(stateDto, selectedPair);
        }

        // --- Persist new server-side state (must happen before lists are stripped) ---
        persistServerState(exerciseVertex, updatedInput);

        // Strip full option lists from the response: the client manages them locally
        // after the initial load.  Only the replacement pair (carried in feedback) is sent.
        updatedInput.setLeftOptions(new ArrayList<>());
        updatedInput.setRightOptions(new ArrayList<>());

        // --- Build exercise response ---
        var exercise = new ExerciseDto();
        exercise.setId(context.getExerciseId());
        exercise.setInput(updatedInput);

        // Only mark the overall exercise "complete" when maxPairs reached
        var overallFeedback = ExerciseFeedbackHelper.createCorrectFeedback(isCorrect);
        overallFeedback.setAnswerId(answerId);
        if (isCorrect && updatedInput.getAnsweredCount() >= updatedInput.getMaxPairs()) {
            overallFeedback.setMessage("Exercise complete");
        } else {
            overallFeedback.setMessage(isCorrect ? "Correct" : "Incorrect");
        }
        exercise.setFeedback(overallFeedback);

        return exercise;
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private ExerciseInputMultiStagePairOptionsDto buildCorrectResult(
            ExerciseInputMultiStagePairOptionsDto original,
            List<PairVertex> allPairs,
            SelectedPair matched) {

        var dto = shallowCopy(original);

        // 1. Increment answered count
        int newAnsweredCount = original.getAnsweredCount() + 1;
        dto.setAnsweredCount(newAnsweredCount);

        // 2. Track matched pair
        var newMatchedIds = new ArrayList<>(original.getMatchedPairLeftIds());
        newMatchedIds.add(matched.getLeftId());
        dto.setMatchedPairLeftIds(newMatchedIds);

        // 3. Remove matched pair from visible columns
        var newLeft = new ArrayList<>(original.getLeftOptions());
        var newRight = new ArrayList<>(original.getRightOptions());
        newLeft.removeIf(o -> o.getId().equals(matched.getLeftId()));
        newRight.removeIf(o -> o.getId().equals(matched.getRightId()));

        // 4. Add replacement pair if still below maxPairs
        if (newAnsweredCount < dto.getMaxPairs()) {
            // Build a single exclusion set: every left ID that has ever been shown
            // (currently visible BEFORE removal + all matched IDs including the current one).
            // Using the pre-removal visible list means the current match is automatically
            // covered without relying on removeIf having run first.
            Set<String> excludedLeftIds = new HashSet<>();
            original.getLeftOptions().forEach(o -> excludedLeftIds.add(o.getId())); // all currently visible
            excludedLeftIds.addAll(newMatchedIds); // all matched (includes just-matched pair)

            PairVertex replacement = allPairs.stream()
                    .filter(p -> !excludedLeftIds.contains(p.getLeftContent().getId()))
                    .findFirst()
                    .orElse(null);

            if (replacement != null) {
                var replacementLeft = buildTextOption(replacement.getLeftContent());
                var replacementRight = buildTextOption(replacement.getRightContent());
                newLeft.add(replacementLeft);
                newRight.add(replacementRight);

                // 5a. Set single-pair feedback with replacement so client can do in-place swap
                var feedback = new MultiStagePairOptionsFeedback();
                feedback.setCorrectPairs(List.of(new FeedbackPair(matched.getLeftId(), matched.getRightId())));
                feedback.setIncorrectPairs(List.of());
                feedback.setReplacementLeft(replacementLeft);
                feedback.setReplacementRight(replacementRight);
                dto.setFeedback(feedback);
            } else {
                // No more replacements available
                var feedback = new MultiStagePairOptionsFeedback();
                feedback.setCorrectPairs(List.of(new FeedbackPair(matched.getLeftId(), matched.getRightId())));
                feedback.setIncorrectPairs(List.of());
                dto.setFeedback(feedback);
            }
        } else {
            // maxPairs reached — no replacement needed
            var feedback = new MultiStagePairOptionsFeedback();
            feedback.setCorrectPairs(List.of(new FeedbackPair(matched.getLeftId(), matched.getRightId())));
            feedback.setIncorrectPairs(List.of());
            dto.setFeedback(feedback);
        }

        dto.setLeftOptions(newLeft);
        dto.setRightOptions(newRight);

        dto.setSelectedPair(null);
        return dto;
    }

    private ExerciseInputMultiStagePairOptionsDto buildIncorrectResult(
            ExerciseInputMultiStagePairOptionsDto original,
            SelectedPair attempted) {

        var dto = shallowCopy(original);

        // Visible columns unchanged
        dto.setLeftOptions(new ArrayList<>(original.getLeftOptions()));
        dto.setRightOptions(new ArrayList<>(original.getRightOptions()));
        dto.setMatchedPairLeftIds(new ArrayList<>(original.getMatchedPairLeftIds()));

        var feedback = new MultiStagePairOptionsFeedback();
        feedback.setCorrectPairs(List.of());
        feedback.setIncorrectPairs(List.of(new FeedbackPair(attempted.getLeftId(), attempted.getRightId())));
        dto.setFeedback(feedback);

        dto.setSelectedPair(null);
        return dto;
    }

    private ExerciseInputMultiStagePairOptionsDto shallowCopy(ExerciseInputMultiStagePairOptionsDto source) {
        var copy = new ExerciseInputMultiStagePairOptionsDto();
        copy.setId(source.getId());
        copy.setSessionId(source.getSessionId());
        copy.setLeftOptionType(source.getLeftOptionType());
        copy.setRightOptionType(source.getRightOptionType());
        copy.setAnsweredCount(source.getAnsweredCount());
        copy.setMaxPairs(source.getMaxPairs());
        return copy;
    }

    // -----------------------------------------------------------------------
    // Server-state helpers
    // -----------------------------------------------------------------------

    /**
     * Reconstructs the current exercise input state from properties stored on the exercise vertex.
     * This avoids relying on the client to round-trip all input data.
     */
    private ExerciseInputMultiStagePairOptionsDto loadServerState(
            ExerciseVertex exerciseVertex, List<PairVertex> allPairs) {

        Map<String, PairOptionsInputOptionDto> leftById = allPairs.stream()
                .collect(Collectors.toMap(
                        p -> p.getLeftContent().getId(),
                        p -> buildTextOption(p.getLeftContent())));
        Map<String, PairOptionsInputOptionDto> rightById = allPairs.stream()
                .collect(Collectors.toMap(
                        p -> p.getRightContent().getId(),
                        p -> buildTextOption(p.getRightContent())));

        List<PairOptionsInputOptionDto> leftOptions = exerciseVertex.getMsCurrentLeftOptionIds().stream()
                .map(leftById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
        List<PairOptionsInputOptionDto> rightOptions = exerciseVertex.getMsCurrentRightOptionIds().stream()
                .map(rightById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));

        var dto = new ExerciseInputMultiStagePairOptionsDto();
        dto.setAnsweredCount(exerciseVertex.getMsAnsweredCount());
        dto.setMaxPairs(10);
        dto.setMatchedPairLeftIds(exerciseVertex.getMsMatchedLeftIds());
        dto.setLeftOptions(leftOptions);
        dto.setRightOptions(rightOptions);
        return dto;
    }

    /**
     * Persists the updated exercise state back to the exercise vertex after evaluation.
     */
    private void persistServerState(
            ExerciseVertex exerciseVertex, ExerciseInputMultiStagePairOptionsDto updatedInput) {

        exerciseVertex.setMsAnsweredCount(updatedInput.getAnsweredCount());
        exerciseVertex.setMsMatchedLeftIds(updatedInput.getMatchedPairLeftIds());
        exerciseVertex.setMsCurrentLeftOptionIds(
                updatedInput.getLeftOptions().stream()
                        .map(PairOptionsInputOptionDto::getId)
                        .collect(Collectors.toList()));
        exerciseVertex.setMsCurrentRightOptionIds(
                updatedInput.getRightOptions().stream()
                        .map(PairOptionsInputOptionDto::getId)
                        .collect(Collectors.toList()));
    }

    private PairOptionsInputTextOptionDto buildTextOption(
            com.explik.diybirdyapp.persistence.vertex.ContentVertex content) {
        if (!(content instanceof TextContentVertex textContent)) {
            throw new IllegalArgumentException(
                    "Only text content is supported for multi-stage tap-pairs, got: "
                            + content.getClass().getSimpleName());
        }
        var opt = new PairOptionsInputTextOptionDto();
        opt.setId(textContent.getId());
        opt.setText(textContent.getValue());
        return opt;
    }
}
