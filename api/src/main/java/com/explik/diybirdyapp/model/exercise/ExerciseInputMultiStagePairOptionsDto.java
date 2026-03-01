package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for the multi-stage tap-pairs exercise input.
 * The user matches left options to right options one pair at a time.
 * Each correct match is replaced with a new pair until maxPairs have been answered.
 */
public class ExerciseInputMultiStagePairOptionsDto extends ExerciseInputDto {

    @NotNull(message = "leftOptionType.required")
    private String leftOptionType = "text";

    @NotNull(message = "rightOptionType.required")
    private String rightOptionType = "text";

    @NotNull(message = "leftOptions.required")
    private List<PairOptionsInputOptionDto> leftOptions = new ArrayList<>();

    @NotNull(message = "rightOptions.required")
    private List<PairOptionsInputOptionDto> rightOptions = new ArrayList<>();

    /** How many pairs have been correctly matched so far. */
    private int answeredCount = 0;

    /** Maximum pairs to be matched to complete the exercise. */
    private int maxPairs = 10;

    /**
     * Left IDs of all pairs that have already been matched (consumed).
     * Used server-side to determine which pairs are still available to show.
     */
    private List<String> matchedPairLeftIds = new ArrayList<>();

    /** Set by the client when submitting: the pair the user selected. Null when returning from server. */
    private SelectedPair selectedPair;

    /** Feedback for the most recent submission. Null until first answer. */
    private MultiStagePairOptionsFeedback feedback;

    public ExerciseInputMultiStagePairOptionsDto() {
        setType(ExerciseInputTypes.MULTI_STAGE_PAIR_OPTIONS);
    }

    // --- Getters / Setters ---

    public String getLeftOptionType() { return leftOptionType; }
    public void setLeftOptionType(String leftOptionType) { this.leftOptionType = leftOptionType; }

    public String getRightOptionType() { return rightOptionType; }
    public void setRightOptionType(String rightOptionType) { this.rightOptionType = rightOptionType; }

    public List<PairOptionsInputOptionDto> getLeftOptions() { return leftOptions; }
    public void setLeftOptions(List<PairOptionsInputOptionDto> leftOptions) { this.leftOptions = leftOptions; }

    public List<PairOptionsInputOptionDto> getRightOptions() { return rightOptions; }
    public void setRightOptions(List<PairOptionsInputOptionDto> rightOptions) { this.rightOptions = rightOptions; }

    public int getAnsweredCount() { return answeredCount; }
    public void setAnsweredCount(int answeredCount) { this.answeredCount = answeredCount; }

    public int getMaxPairs() { return maxPairs; }
    public void setMaxPairs(int maxPairs) { this.maxPairs = maxPairs; }

    public List<String> getMatchedPairLeftIds() { return matchedPairLeftIds; }
    public void setMatchedPairLeftIds(List<String> matchedPairLeftIds) { this.matchedPairLeftIds = matchedPairLeftIds; }

    public SelectedPair getSelectedPair() { return selectedPair; }
    public void setSelectedPair(SelectedPair selectedPair) { this.selectedPair = selectedPair; }

    public MultiStagePairOptionsFeedback getFeedback() { return feedback; }
    public void setFeedback(MultiStagePairOptionsFeedback feedback) { this.feedback = feedback; }

    // --- Nested types ---

    /** The pair the user selected, sent as part of the answer submission. */
    public static class SelectedPair {
        @NotNull(message = "leftId.required")
        private String leftId;

        @NotNull(message = "rightId.required")
        private String rightId;

        public String getLeftId() { return leftId; }
        public void setLeftId(String leftId) { this.leftId = leftId; }

        public String getRightId() { return rightId; }
        public void setRightId(String rightId) { this.rightId = rightId; }
    }

    /** Feedback for the most recent single-pair submission. */
    public static class MultiStagePairOptionsFeedback {
        private List<FeedbackPair> correctPairs = new ArrayList<>();
        private List<FeedbackPair> incorrectPairs = new ArrayList<>();

        /** Replacement option for the left column (only set on a correct match, when a replacement exists). */
        private PairOptionsInputTextOptionDto replacementLeft;

        /** Replacement option for the right column (only set on a correct match, when a replacement exists). */
        private PairOptionsInputTextOptionDto replacementRight;

        public List<FeedbackPair> getCorrectPairs() { return correctPairs; }
        public void setCorrectPairs(List<FeedbackPair> correctPairs) { this.correctPairs = correctPairs; }

        public List<FeedbackPair> getIncorrectPairs() { return incorrectPairs; }
        public void setIncorrectPairs(List<FeedbackPair> incorrectPairs) { this.incorrectPairs = incorrectPairs; }

        public PairOptionsInputTextOptionDto getReplacementLeft() { return replacementLeft; }
        public void setReplacementLeft(PairOptionsInputTextOptionDto replacementLeft) { this.replacementLeft = replacementLeft; }

        public PairOptionsInputTextOptionDto getReplacementRight() { return replacementRight; }
        public void setReplacementRight(PairOptionsInputTextOptionDto replacementRight) { this.replacementRight = replacementRight; }
    }

    public static class FeedbackPair {
        private String leftId;
        private String rightId;

        public FeedbackPair() {}
        public FeedbackPair(String leftId, String rightId) {
            this.leftId = leftId;
            this.rightId = rightId;
        }

        public String getLeftId() { return leftId; }
        public void setLeftId(String leftId) { this.leftId = leftId; }

        public String getRightId() { return rightId; }
        public void setRightId(String rightId) { this.rightId = rightId; }
    }

    @JsonSubTypes({
            @JsonSubTypes.Type(value = PairOptionsInputTextOptionDto.class, name = "text")
    })
    public static abstract class PairOptionsInputOptionDto {
        @NotNull(message = "id.required")
        private String id;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    public static class PairOptionsInputTextOptionDto extends PairOptionsInputOptionDto {
        @NotNull(message = "text.required")
        private String text;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}
