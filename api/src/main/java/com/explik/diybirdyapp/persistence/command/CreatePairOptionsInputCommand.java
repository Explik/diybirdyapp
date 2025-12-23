package com.explik.diybirdyapp.persistence.command;

import java.util.List;

/**
 * Domain command to create pair options (matching) input for an exercise.
 * Users must match items from two columns.
 */
public class CreatePairOptionsInputCommand implements AtomicCommand {
    private List<PairDefinition> pairs;

    public List<PairDefinition> getPairs() {
        return pairs;
    }

    public void setPairs(List<PairDefinition> pairs) {
        this.pairs = pairs;
    }

    /**
     * Represents a pair of content items to be matched.
     */
    public static class PairDefinition {
        private String leftId;
        private String rightId;

        public PairDefinition() {}

        public PairDefinition(String leftId, String rightId) {
            this.leftId = leftId;
            this.rightId = rightId;
        }

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
