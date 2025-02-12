package com.explik.diybirdyapp.controller.dto.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ExerciseInputPairOptionsDto extends ExerciseInputDto {
    @NotNull
    private String leftOptionType = "text";

    @NotNull
    private String rightOptionType = "text";

    @NotNull
    private List<PairOptionsInputOptionDto> leftOptions = List.of();

    @NotNull
    private List<PairOptionsInputOptionDto> rightOptions = List.of();

    private PairOptionsInputFeedback feedback;

    public ExerciseInputPairOptionsDto() {
        setType(ExerciseInputTypes.PAIR_OPTIONS);
    }

    public String getLeftOptionType() {
        return leftOptionType;
    }

    public void setLeftOptionType(String leftOptionType) {
        this.leftOptionType = leftOptionType;
    }

    public String getRightOptionType() {
        return rightOptionType;
    }

    public void setRightOptionType(String rightOptionType) {
        this.rightOptionType = rightOptionType;
    }

    public List<PairOptionsInputOptionDto> getLeftOptions() {
        return leftOptions;
    }

    public void setLeftOptions(List<PairOptionsInputOptionDto> leftOptions) {
        this.leftOptions = leftOptions;
    }

    public List<PairOptionsInputOptionDto> getRightOptions() {
        return rightOptions;
    }

    public void setRightOptions(List<PairOptionsInputOptionDto> rightOptions) {
        this.rightOptions = rightOptions;
    }

    public PairOptionsInputFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(PairOptionsInputFeedback feedback) {
        this.feedback = feedback;
    }

    public static abstract class PairOptionsInputOptionDto {
        @NotNull
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class PairOptionsInputTextOptionDto extends PairOptionsInputOptionDto {
        @NotNull
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class PairOptionsInputFeedback {
        @NotNull
        private List<PairOptionFeedbackPair> correctPairs = List.of();

        @NotNull
        private List<PairOptionFeedbackPair> incorrectPairs = List.of();

        public List<PairOptionFeedbackPair> getCorrectPairs() {
            return correctPairs;
        }

        public void setCorrectPairs(List<PairOptionFeedbackPair> correctPairs) {
            this.correctPairs = correctPairs;
        }

        public List<PairOptionFeedbackPair> getIncorrectPairs() {
            return incorrectPairs;
        }

        public void setIncorrectPairs(List<PairOptionFeedbackPair> incorrectPairs) {
            this.incorrectPairs = incorrectPairs;
        }

        public static class PairOptionFeedbackPair {
            @NotNull
            public String leftId;

            @NotNull
            public String rightId;

            public String getLeftId() {
                return leftId;
            }

            public void setLeftId(String leftId) {
                this.leftId = leftId;
            }

            public String getRightId() {
                return rightId;
            }

            public void setRightId(String rightId) {
                this.rightId = rightId;
            }
        }
    }
}
