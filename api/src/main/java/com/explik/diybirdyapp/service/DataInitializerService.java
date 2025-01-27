package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.builder.*;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperationsReviewFlashcardDeck;
import com.explik.diybirdyapp.persistence.vertexFactory.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializerService {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private ExerciseWriteSentenceUsingWordVertexFactory exerciseWriteSentenceUsingWordVertexFactory;

    @Autowired
    private ExerciseWriteTranslatedSentenceVertexFactory exerciseWriteTranslatedSentenceVertexFactory;

    @Autowired
    private ExerciseSelectFlashcardVertexFactory exerciseSelectFlashcardVertexFactory;

    @Autowired
    private ExercisePronounceFlashcardVertexFactory exercisePronounceFlashcardVertexFactory;

    @Autowired
    private ExerciseReviewFlashcardVertexFactory exerciseReviewFlashcardVertexFactory;

    @Autowired
    private ExerciseSessionOperationsReviewFlashcardDeck exerciseSessionFlashcardReviewVertexFactory;

    @Autowired
    private LanguageVertexFactory languageVertexFactory;

    @Autowired
    private TextToSpeechConfigVertexFactory textToSpeechConfigVertexFactory;

    @Autowired
    private WordVertexFactory wordVertexFactory;

    @Autowired
    private PronunciationVertexFactory pronunciationVertexFactory;

    @Autowired
    private AudioContentVertexFactory audioContentVertexFactory;

    @Autowired
    private ImageContentVertexFactory imageContentVertexFactory;

    @Autowired
    private VideoContentVertexFactory videoImageContentVertexFactory;

    @Autowired
    private VertexBuilderFactory builderFactory;

    public void resetInitialData() {
        traversalSource.V().drop().iterate();
        appendInitialData();
    }

    public void appendInitialData() {
        addInitialLanguageData();
        addInitialContentAndConcepts();
        addInitialExerciseData();
    }

    public void addInitialLanguageData() {
        builderFactory.createLanguageVertexBuilder()
                .withId("langVertex1")
                .withName("Danish")
                .withAbbreviation("DA")
                .withGoogleTextToSpeech("da-DK", "da-DK-Wavenet-A")
                .build(traversalSource);

        builderFactory.createLanguageVertexBuilder()
                .withId("langVertex2")
                .withName("English")
                .withAbbreviation("EN")
                .withGoogleTextToSpeech("en-US", "en-US-Wavenet-A")
                .build(traversalSource);
    }

    public void addInitialContentAndConcepts() {
        var langVertex1 = LanguageVertex.findByAbbreviation(traversalSource, "DA");
        var langVertex2 = LanguageVertex.findByAbbreviation(traversalSource, "EN");

        builderFactory.createTextContentVertexBuilder()
                .withValue("Bereshit")
                .withLanguage(langVertex1)
                .build(traversalSource);

        // Text content
        var textVertex0 = TextContentVertex.create(traversalSource);
        textVertex0.setId("textVertex0");
        textVertex0.setValue("Bereshit");
        textVertex0.setLanguage(langVertex1);

        var imageVertex1 = imageContentVertexFactory.create(
                traversalSource,
                new ImageContentVertexFactory.Options("imageVertex1", "https://fastly.picsum.photos/id/17/2500/1667.jpg?hmac=HD-JrnNUZjFiP2UZQvWcKrgLoC_pc_ouUSWv8kHsJJY"));

        var audioContentVertex1 = audioContentVertexFactory.create(
                traversalSource,
                new AudioContentVertexFactory.Options("audioContentVertex1", "https://github.com/rafaelreis-hotmart/Audio-Sample-files/raw/master/sample.mp3", langVertex1));

        var videoContentVertex1 = videoImageContentVertexFactory.create(
                traversalSource,
                new VideoContentVertexFactory.Options("videoContentVertex1", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", langVertex2));

        // Flashcard deck 1
        builderFactory.createFlashcardDeckVertexBuilder()
                .withId("flashcardDeckVertex1")
                .withName("First ever flashcard deck")
                .withDefaultLanguages(langVertex1, langVertex2)
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText("Hej verden")
                        .withBackText("Hello world"))
                .build(traversalSource);

        // Flashcard deck 2
        builderFactory.createFlashcardDeckVertexBuilder()
                .withId("flashcardDeckVertex2")
                .withName("Second ever flashcard deck")
                .withDefaultLanguages(langVertex1, langVertex2)
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText("Hej John")
                        .withBackText("Hey John"))
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText("Hej billede")
                        .withBackContent(imageVertex1))
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText("Hej lyd")
                        .withBackContent(audioContentVertex1))
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText("Hej video")
                        .withBackContent(videoContentVertex1))
                .build(traversalSource);

        // Flashcard deck 3
        builderFactory.createFlashcardDeckVertexBuilder()
                .withId("textOnlyFlashcardDeck")
                .withName("Text only flashcard deck")
                .withDefaultLanguages(langVertex1, langVertex2)
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText("Hej")
                        .withBackText("Hello"))
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText("Verden")
                        .withBackText("World"))
                .build(traversalSource);

        // Word concepts
        var wordVertex1 = WordVertex.create(traversalSource);
        wordVertex1.setId("wordVertex1");
        wordVertex1.setValue(textVertex0.getValue());
        wordVertex1.setLanguage(textVertex0.getLanguage());
        wordVertex1.addExample(textVertex0);
        wordVertex1.setMainExample(textVertex0);

        // Pronunciation concept
        var pronunciationVertex1 = pronunciationVertexFactory.create(
                traversalSource,
                new PronunciationVertexFactory.Options("pronunciationVertex1", textVertex0, audioContentVertex1));
    }

    public void addInitialExerciseData() {
        var langVertex = LanguageVertex.findByAbbreviation(traversalSource, "EN");

        var wordVertex1 = builderFactory.createTextContentVertexBuilder()
                .withId("1")
                .withValue("example")
                .withLanguage(langVertex)
                .build(traversalSource);

        exerciseWriteSentenceUsingWordVertexFactory.create(
                traversalSource,
                new ExerciseWriteSentenceUsingWordVertexFactory.Options("1", "example", wordVertex1));

        // Exercise 2 - Translate sentence to Danish
        var wordVertex2 = builderFactory.createTextContentVertexBuilder()
                .withId("2")
                .withValue("This is an example sentence")
                .withLanguage(langVertex)
                .build(traversalSource);

        exerciseWriteTranslatedSentenceVertexFactory.create(
                traversalSource,
                new ExerciseWriteTranslatedSentenceVertexFactory.Options("2", "Danish", wordVertex2));

        // Exercise 3 - Multiple choice text exercise
        var flashcardVertex1 = builderFactory.createFlashcardVertexBuilder()
                .withId("flashcardVertex1")
                .withFrontText("Correct option", langVertex)
                .withBackText("Correct option", langVertex)
                .build(traversalSource);

        var flashcardVertex2 = builderFactory.createFlashcardVertexBuilder()
                .withFrontText("Random option 1", langVertex)
                .withBackText("Random option 1", langVertex)
                .build(traversalSource);

        exerciseSelectFlashcardVertexFactory.create(
                traversalSource,
                new ExerciseSelectFlashcardVertexFactory.Options(
                        "3",
                        null,
                        flashcardVertex1,
                        List.of(flashcardVertex2),
                        "front"));

        // Exercise session 4
        var flashcardVertex = FlashcardVertex.findById(traversalSource, "flashcardVertex1");
        exerciseReviewFlashcardVertexFactory.create(
                traversalSource,
                new ExerciseReviewFlashcardVertexFactory.Options("4", null, flashcardVertex));

        // Exercise session 4 - Flashcard exercise session
        var sessionModel = new ExerciseSessionModel();
        sessionModel.setId("4");
        sessionModel.setType("flashcard-review-session");
        sessionModel.setFlashcardDeckId("flashcardDeckVertex1");

        exerciseSessionFlashcardReviewVertexFactory.init(
                traversalSource,
                sessionModel);

        // Exercise session 5 - Flashcard pronounce exercise
        var flashcardVertex3 = builderFactory.createFlashcardVertexBuilder()
                .withId("flashcardVertex3")
                .withFrontContent(wordVertex1)
                .withBackContent(wordVertex1)
                .build(traversalSource);

        exercisePronounceFlashcardVertexFactory.create(
                traversalSource,
                new ExercisePronounceFlashcardVertexFactory.Options("5", null, flashcardVertex3, "front"));
    }
}
