package com.explik.diybirdyapp.command.dto;

import java.util.List;

public class ImportFlashcardDeckDTO {
    private String id;
    private String name;
    private String description;
    private List<Flashcard> flashcards;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    public static class Flashcard {
        private String leftValue;
        private String rightValue;
        private Language leftLanguage;
        private Language rightLanguage;

        public String getLeftValue() {
            return leftValue;
        }

        public void setLeftValue(String leftValue) {
            this.leftValue = leftValue;
        }

        public String getRightValue() {
            return rightValue;
        }

        public void setRightValue(String rightValue) {
            this.rightValue = rightValue;
        }

        public Language getLeftLanguage() {
            return leftLanguage;
        }

        public void setLeftLanguage(Language leftLanguage) {
            this.leftLanguage = leftLanguage;
        }

        public Language getRightLanguage() {
            return rightLanguage;
        }

        public void setRightLanguage(Language rightLanguage) {
            this.rightLanguage = rightLanguage;
        }

        public static class Language {
            private String id;
            private String name;
            private String abbreviation;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAbbreviation() {
                return abbreviation;
            }

            public void setAbbreviation(String abbreviation) {
                this.abbreviation = abbreviation;
            }
        }
    }
}