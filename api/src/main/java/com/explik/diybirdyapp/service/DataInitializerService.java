package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.persistence.builder.*;
import com.explik.diybirdyapp.persistence.command.*;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.schema.ExerciseSchemas;
import com.explik.diybirdyapp.persistence.schema.parameter.*;
import com.explik.diybirdyapp.persistence.vertex.*;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperationsReviewFlashcardDeck;
import com.explik.diybirdyapp.persistence.command.CreateExerciseCommand;
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
    private CommandHandler<CreateImageContentVertexCommand> createImageContentVertexCommandHandler;

    @Autowired
    private CommandHandler<CreateAudioContentVertexCommand> createAudioContentVertexCommandCommandHandler;

    @Autowired
    private CommandHandler<CreateLanguageVertexCommand> createLanguageVertexCommandHandler;

    @Autowired
    private ExerciseSessionOperationsReviewFlashcardDeck exerciseSessionFlashcardReviewVertexFactory;

    @Autowired
    private CommandHandler<CreatePronunciationVertexCommand> createPronunciationVertexCommandCommandHandler;

    @Autowired
    private CommandHandler<CreateVideoContentVertexCommand> createVideoContentVertexCommandHandler;

    @Autowired
    private VertexBuilderFactory builderFactory;

    @Autowired
    private ExerciseCreationService exerciseCreationService;

    @Autowired
    private CommandHandler<CreateExerciseCommand> createExerciseCommandHandler;

    @Autowired
    private CommandHandler<CreateUserRoleCommand> createUserRoleCommandHandler;

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
                .withIsoCode("DA")
                .withGoogleTranslate("da-DK")
                .withGoogleTextToSpeech("da-DK", "da-DK-Wavenet-A")
                .withGoogleSpeechToText("da-DK")
                .build(traversalSource);

        builderFactory.createLanguageVertexBuilder()
                .withId("langVertex2")
                .withName("English")
                .withIsoCode("EN")
                .withGoogleTranslate("en-US")
                .withGoogleTextToSpeech("en-US", "en-US-Wavenet-A")
                .withGoogleSpeechToText("en-US")
                .build(traversalSource);

        builderFactory.createLanguageVertexBuilder()
                .withId("langVertex3")
                .withName("Chinese")
                .withIsoCode("ZH")
                .withGoogleTranslate("zh-CN")
                .withGoogleTextToSpeech("cmn-cn", "cmn-CN-Chirp3-HD-Achird")
                .withGoogleSpeechToText("cmn-CN")
                .build(traversalSource);
    }

    public void addInitialContentAndConcepts() {
        var langVertex1 = LanguageVertex.findByIsoCode(traversalSource, "DA");
        var langVertex2 = LanguageVertex.findByIsoCode(traversalSource, "EN");

        builderFactory.createTextContentVertexBuilder()
                .withValue("Bereshit")
                .withLanguage(langVertex1)
                .build(traversalSource);

        // Text content
        var textVertex0 = TextContentVertex.create(traversalSource);
        textVertex0.setId("textVertex0");
        textVertex0.setValue("Bereshit");
        textVertex0.setLanguage(langVertex1);

        var createImageCommand = new CreateImageContentVertexCommand();
        createImageCommand.setId("imageVertex1");
        createImageCommand.setUrl("https://fastly.picsum.photos/id/17/2500/1667.jpg?hmac=HD-JrnNUZjFiP2UZQvWcKrgLoC_pc_ouUSWv8kHsJJY");
        createImageContentVertexCommandHandler.handle(createImageCommand);

        var imageVertex1 = ImageContentVertex.findById(traversalSource, "imageVertex1");

        var createAudioCommand = new CreateAudioContentVertexCommand();
        createAudioCommand.setId("audioContentVertex1");
        createAudioCommand.setUrl("https://github.com/rafaelreis-hotmart/Audio-Sample-files/raw/master/sample.mp3");
        createAudioCommand.setLanguageVertexId(langVertex1.getId());
        createAudioContentVertexCommandCommandHandler.handle(createAudioCommand);

        var audioContentVertex1 = AudioContentVertex.getById(traversalSource, "audioContentVertex1");

        var createVideoCommand = new CreateVideoContentVertexCommand();
        createVideoCommand.setId("videoContentVertex1");
        createVideoCommand.setUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        createVideoCommand.setLanguageVertexId(langVertex2.getId());
        createVideoContentVertexCommandHandler.handle(createVideoCommand);

        var videoContentVertex1 = VideoContentVertex.getById(traversalSource, "videoContentVertex1");

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
    }

    public void addInitialExerciseData() {
        // Setting up exercise content
        var langVertex = LanguageVertex.findByIsoCode(traversalSource, "EN");

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

        var createAudioCommand = new CreateAudioContentVertexCommand();
        createAudioCommand.setId("audioContentVertex1");
        createAudioCommand.setUrl("https://github.com/rafaelreis-hotmart/Audio-Sample-files/raw/master/sample.mp3");
        createAudioCommand.setLanguageVertexId(langVertex.getId());
        createAudioContentVertexCommandCommandHandler.handle(createAudioCommand);

        var audioContentVertex1 = AudioContentVertex.getById(traversalSource, "audioContentVertex1");

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

        var audioContentParameters = new ExerciseContentParameters()
                .withContent(audioContentVertex1);

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
        var writeSentenceCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.WRITE_SENTENCE_USING_WORD_EXERCISE, writeSentenceUsingWordParameters);
        createExerciseCommandHandler.handle(writeSentenceCommand);

        // Exercise - Write translated sentence
        var writeTranslatedSentenceExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.WRITE_TRANSLATED_SENTENCE)
                .withSession(null)
                .withTargetLanguage("Danish")
                .withContent(longTextContentParameters);
        var writeTranslatedCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.WRITE_TRANSLATED_SENTENCE_EXERCISE, writeTranslatedSentenceExerciseParameters);
        createExerciseCommandHandler.handle(writeTranslatedCommand);

        // Exercise - Select flashcard exercise
        var selectFlashcardExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.SELECT_FLASHCARD)
                .withSession(null)
                .withContent(flashcardSideContentParameters)
                .withSelectOptionsInput(selectFlashcardParameters);
        var selectFlashcardCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.SELECT_FLASHCARD_EXERCISE, selectFlashcardExerciseParameters);
        createExerciseCommandHandler.handle(selectFlashcardCommand);

        // Exercise - Listen and select exercise
        var listenAndSelectFlashcardExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.LISTEN_AND_SELECT)
                .withSession(null)
                .withContent(audioContentParameters)
                .withSelectOptionsInput(selectFlashcardParameters);
        var listenAndSelectCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.LISTEN_AND_SELECT_EXERCISE, listenAndSelectFlashcardExerciseParameters);
        createExerciseCommandHandler.handle(listenAndSelectCommand);

        // Exercise - Listen and write exercise
        var listenAndWriteFlashcardExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.LISTEN_AND_WRITE)
                .withSession(null)
                .withContent(audioContentParameters);
        var listenAndWriteCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.LISTEN_AND_WRITE_EXERCISE, listenAndWriteFlashcardExerciseParameters);
        createExerciseCommandHandler.handle(listenAndWriteCommand);

        // Exercise - Flashcard review exercise
        var reviewFlashcardExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.REVIEW_FLASHCARD)
                .withSession(null)
                .withContent(flashcardContentParameters);
        var reviewFlashcardCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.REVIEW_FLASHCARD_EXERCISE, reviewFlashcardExerciseParameters);
        createExerciseCommandHandler.handle(reviewFlashcardCommand);

        // Exercise - Write flashcard exercise
        var writeFlashcardExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.WRITE_FLASHCARD)
                .withSession(null)
                .withContent(flashcardSideContentParameters);
        var writeFlashcardCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.WRITE_FLASHCARD_EXERCISE, writeFlashcardExerciseParameters);
        createExerciseCommandHandler.handle(writeFlashcardCommand);

        // Exercise - Flashcard pronounce exercise
        var pronounceFlashcardExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.PRONOUNCE_FLASHCARD)
                .withSession(null)
                .withContent(flashcardContentParameters);
        var pronounceFlashcardCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.PRONOUNCE_FLASHCARD_EXERCISE, pronounceFlashcardExerciseParameters);
        createExerciseCommandHandler.handle(pronounceFlashcardCommand);

        // Exercise - Arrange all words in translation exercise
        var arrangeWordsInTranslationExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.ARRANGE_WORDS_IN_TRANSLATION)
                .withSession(null)
                .withContent(longTextContentParameters)
                .withArrangeTextOptionsInput(arrangeTextOptionsParameters);
        var arrangeWordsCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.ARRANGE_WORDS_IN_TRANSLATION, arrangeWordsInTranslationExerciseParameters);
        createExerciseCommandHandler.handle(arrangeWordsCommand);

        // Exercise - Tap pairs exercise
        var tapPairsExerciseParameters = new ExerciseParameters()
                .withId(ExerciseTypes.TAP_PAIRS)
                .withSession(null)
                .withPairOptionsInput(pairFlashcardParameters);
        var tapPairsCommand = exerciseCreationService.createExerciseCommand(ExerciseSchemas.TAP_PAIRS_EXERCISE, tapPairsExerciseParameters);
        createExerciseCommandHandler.handle(tapPairsCommand);
    }

    public void addInitialUserData() {
        // Create user role "ROLE_USER"
        var command = new CreateUserRoleCommand();
        command.setRoleName("ROLE_USER");
        createUserRoleCommandHandler.handle(command);
    }
}
