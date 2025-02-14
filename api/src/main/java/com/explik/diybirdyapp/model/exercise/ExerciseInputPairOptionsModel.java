package com.explik.diybirdyapp.model.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExerciseInputPairOptionsModel extends ExerciseInputModel {
    private List<OptionPair> pairs = new ArrayList<>();

    public ExerciseInputPairOptionsModel() {
        setType(ExerciseInputTypes.PAIR_OPTIONS);
    }

    public List<OptionPair> getPairs() {
        return pairs;
    }

    public void setPairs(List<OptionPair> pairs) {
        this.pairs = pairs;
    }

    public void addOptionPair(OptionPair pair) {
        pairs.add(pair);
    }

    public String getLeftOptionType() {
        return "text";
    }

    public String getRightOptionType() {
        return "text";
    }

    public List<Option> getLeftOptions() {
       return pairs.stream().map(OptionPair::getLeft).collect(Collectors.toList());
    }

    public List<Option> getRightOptions() {
        return pairs.stream().map(OptionPair::getRight).collect(Collectors.toList());
    }

    public static class Option {
        private String id;
        private String text;

        public Option(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class OptionPair {
        private Option left;
        private Option right;

        public OptionPair(Option left, Option right) {
            this.left = left;
            this.right = right;
        }

        public Option getLeft() {
            return left;
        }

        public void setLeft(Option left) {
            this.left = left;
        }

        public Option getRight() {
            return right;
        }

        public void setRight(Option right) {
            this.right = right;
        }
    }
}
