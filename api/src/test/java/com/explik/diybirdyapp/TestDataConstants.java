package com.explik.diybirdyapp;

public class TestDataConstants {
    // Languages
    public static class Languages {
        public static class English {
            public static final String Id = "1";
            public static final String Name = "English";
            public static final String Abbreviation = "en";

            public static class TextToSpeech {
                public static final String LanguageCode = "en-US";
                public static final String VoiceName = "en-US-Wavenet-A";
            }
        }

        public static class Danish {
            public static final String Id = "2";
            public static final String Name = "Danish";
            public static final String Abbreviation = "da";

            public static class TextToSpeech {
                public static final String LanguageCode = "da-DK";
                public static final String VoiceName = "da-DK-Wavenet-A";
            }
        }
    }

    // General content
    public static class TextContent {
        public static final String Id = "1";
        public static final String Value = "This is an example text";
    }

    public static class Flashcard {
        public static final String Id = "1";
        public static final String FrontText = "Front text 1";
        public static final String BackText = "Back text 1";
    }

    public static class FlashcardDeck {
        public static final String Id = "1";
        public static final String Name = "Flashcard deck 1";

        public static class Flashcard1 {
            public static final String Id = "1";
            public static final String FrontText = "Front text 1";
            public static final String BackText = "Back text 1";
        }

        public static class Flashcard2 {
            public static final String Id = "2";
            public static final String FrontText = "Front text 2";
            public static final String BackText = "Back text 2";
        }
    }

    // Exercises (Each exercise will have its own content)
    public static class WriteSentenceUsingWordExercise {
        public static final String Id = "1";
        public static final String TargetLanguage = Languages.English.Name;
        public static final String Word = "example";
    }

    public static class WriteTranslatedSentenceExercise {
        public static final String Id = "2";
        public static final String TargetLanguage = Languages.Danish.Name;
        public static final String Sentence = "This is an example sentence";
    }

    public static class SelectFlashcardExercise {
        public static final String Id = "3";
        public static final String FlashcardText1 = "Flashcard 1";
        public static final String FlashcardText2 = "Flashcard 2";
        public static final String FlashcardText3 = "Flashcard 3";
        public static final String FlashcardText4 = "Flashcard 4";
    }

    public static class ReviewFlashcardExercise {
        public static final String Id = "4";

        public static class Flashcard {
            public static final String Id = "1";
            public static final String FrontText = "Front text 1";
            public static final String BackText = "Back text 1";
        }
    }
}