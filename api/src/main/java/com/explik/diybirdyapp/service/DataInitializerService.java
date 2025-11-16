package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.builder.*;
import com.explik.diybirdyapp.persistence.repository.UserRepository;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperationsReviewFlashcardDeck;
import com.explik.diybirdyapp.persistence.vertexFactory.*;
import com.explik.diybirdyapp.persistence.vertexFactory.parameter.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializerService {
    @Autowired
    private GraphTraversalSource traversalSource;

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

    @Autowired
    private ExerciseAbstractVertexFactory exerciseAbstractVertexFactory;

    @Autowired
    private UserRepository userRepository;

    public void resetInitialData() {
        traversalSource.V().drop().iterate();
        appendInitialData();
    }

    public void appendInitialData() {
        addInitialLanguageData();
        addInitialContentAndConcepts();
        addInitialExerciseData();
        addInitialUserData();
    }

    public void addInitialLanguageData() {
        builderFactory.createLanguageVertexBuilder()
                .withId("langVertex1")
                .withName("Danish")
                .withAbbreviation("DA")
                .withGoogleTranslate("da-DK")
                .withGoogleTextToSpeech("da-DK", "da-DK-Wavenet-A")
                .build(traversalSource);

        builderFactory.createLanguageVertexBuilder()
                .withId("langVertex2")
                .withName("English")
                .withAbbreviation("EN")
                .withGoogleTranslate("en-US")
                .withGoogleTextToSpeech("en-US", "en-US-Wavenet-A")
                .build(traversalSource);

        builderFactory.createLanguageVertexBuilder()
                .withId("langVertex3")
                .withName("Chinese")
                .withAbbreviation("ZH")
                .withGoogleTranslate("zh-CN")
                .withGoogleTextToSpeech("cmn-cn", "cmn-CN-Chirp3-HD-Achird")
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
                .withName("First ever flashcard deck (public)")
                .withDefaultLanguages(langVertex1, langVertex2)
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText("Hej verden")
                        .withBackText("Hello world"))
                .build(traversalSource);

        // Flashcard deck 2
        builderFactory.createFlashcardDeckVertexBuilder()
                .withId("flashcardDeckVertex2")
                .withName("Second ever flashcard deck (public)")
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
                .withName("Text only flashcard deck (public)")
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
        wordVertex1.setValues(new String[] { textVertex0.getValue() });
        wordVertex1.setLanguage(textVertex0.getLanguage());
        wordVertex1.addExample(textVertex0);
        wordVertex1.setTextContent(textVertex0);

        // Pronunciation concept
        var pronunciationVertex1 = pronunciationVertexFactory.create(
                traversalSource,
                new PronunciationVertexFactory.Options("pronunciationVertex1", textVertex0, audioContentVertex1));
    }

    public void addInitialExerciseData() {
        // Setting up exercise content
        var langVertex = LanguageVertex.findByAbbreviation(traversalSource, "EN");

        var sentenceTextContent = builderFactory.createTextContentVertexBuilder()
                .withId("textContent2")
                .withValue("This is an example sentence")
                .withLanguage(langVertex)
                .build(traversalSource);

        var wordTextContents = new ArrayList<TextContentVertex>();
        for (var word : sentenceTextContent.getValue().split(" ")) {
            var wordTextContent = builderFactory.createTextContentVertexBuilder()
                    .withValue(word)
                    .withLanguage(langVertex)
                    .build(traversalSource);
            wordTextContents.add(wordTextContent);
        }

        var flashcardVertex1 = builderFactory.createFlashcardVertexBuilder()
                .withId("flashcardVertex1")
                .withFrontText("Correct option", langVertex)
                .withBackText("Correct option", langVertex)
                .build(traversalSource);

        var flashcardVertex2 = builderFactory.createFlashcardVertexBuilder()
                .withFrontText("Random option 1", langVertex)
                .withBackText("Random option 1", langVertex)
                .build(traversalSource);

        // Setting up exercise parameters
        var shortTextContentParameters = new ExerciseContentParameters()
                .withContent(builderFactory.createTextContentVertexBuilder()
                        .withId("textContent1")
                        .withValue("Example")
                        .withLanguage(langVertex)
                        .build(traversalSource));
        var longTextContentParameters = new ExerciseContentParameters()
                .withContent(sentenceTextContent);

        var flashcardContentParameters = new ExerciseContentParameters()
                .withContent(flashcardVertex1);

        var flashcardSideContentParameters = new ExerciseContentParameters()
                .withFlashcardContent(flashcardVertex1, "front");

        var arrangeTextOptionsParameters = new ExerciseInputParametersArrangeTextOptions()
                .withOptions(wordTextContents);

        var selectFlashcardParameters = new ExerciseInputParametersSelectOptions()
                        .withCorrectOptions(List.of(flashcardVertex1.getLeftContent()))
                        .withIncorrectOptions(List.of(flashcardVertex2.getRightContent()));

        var pairFlashcardParameters = new ExerciseInputParametersPairOptions()
                .withPairs(List.of(
                        List.of(flashcardVertex1.getLeftContent(), flashcardVertex1.getRightContent()),
                        List.of(flashcardVertex2.getLeftContent(), flashcardVertex2.getRightContent())
                ));

        // Exercise - Write sentence using word
        var writeSentenceUsingWordParameters = new ExerciseParameters()
                .withId(ExerciseTypes.WRITE_SENTENCE_USING_WORD)
                .withSession(null)
                .withTargetLanguage("example")
                .withContent(shortTextContentParameters);
        var writeSentenceUsingWordFactory = exerciseAbstractVertexFactory.create(ExerciseSchemas.WRITE_SENTENCE_USING_WORD_EXERCISE);
        writeSentenceUsingWordFactory.create(traversalSource, writeSentenceUsingWordParameters);

        // Exercise - Write translated sentence
        var writeTranslatedSentenceExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.WRITE_TRANSLATED_SENTENCE)
                .withSession(null)
                .withTargetLanguage("Danish")
                .withContent(longTextContentParameters);
        exerciseAbstractVertexFactory
                .create(ExerciseSchemas.WRITE_TRANSLATED_SENTENCE_EXERCISE)
                .create(traversalSource, writeTranslatedSentenceExerciseParameters);

        // Exercise - Select flashcard exercise
        var selectFlashcardExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.SELECT_FLASHCARD)
                .withSession(null)
                .withContent(flashcardSideContentParameters)
                .withSelectOptionsInput(selectFlashcardParameters);
        exerciseAbstractVertexFactory
                .create(ExerciseSchemas.SELECT_FLASHCARD_EXERCISE)
                .create(traversalSource, selectFlashcardExerciseParameters);

        // Exercise - Flashcard review exercise
        var reviewFlashcardExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.REVIEW_FLASHCARD)
                .withSession(null)
                .withContent(flashcardContentParameters);
        exerciseAbstractVertexFactory
                .create(ExerciseSchemas.REVIEW_FLASHCARD_EXERCISE)
                .create(traversalSource, reviewFlashcardExerciseParameters);

        // Exercise - Flashcard pronounce exercise
        var pronounceFlashcardExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.PRONOUNCE_FLASHCARD)
                .withSession(null)
                .withContent(flashcardContentParameters);
        exerciseAbstractVertexFactory
                .create(ExerciseSchemas.PRONOUNCE_FLASHCARD_EXERCISE)
                .create(traversalSource, pronounceFlashcardExerciseParameters);

        // Exercise - Arrange all words in translation exercise
        var arrangeWordsInTranslationExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.ARRANGE_WORDS_IN_TRANSLATION)
                .withSession(null)
                .withContent(longTextContentParameters)
                .withArrangeTextOptionsInput(arrangeTextOptionsParameters);
        exerciseAbstractVertexFactory
                .create(ExerciseSchemas.ARRANGE_WORDS_IN_TRANSLATION)
                .create(traversalSource, arrangeWordsInTranslationExerciseParameters);

        // Exercise - Tap pairs exercise
        var tapPairsExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.TAP_PAIRS)
                .withSession(null)
                .withPairOptionsInput(pairFlashcardParameters);
        exerciseAbstractVertexFactory
                .create(ExerciseSchemas.TAP_PAIRS_EXERCISE)
                .create(traversalSource, tapPairsExerciseParameters);
    }

    public void addInitialUserData() {
        // Create user role "ROLE_USER"
        userRepository.createUserRole("ROLE_USER");
    }
}
