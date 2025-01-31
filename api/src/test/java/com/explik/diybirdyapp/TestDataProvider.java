package com.explik.diybirdyapp;

import com.explik.diybirdyapp.persistence.builder.FlashcardVertexBuilder;
import com.explik.diybirdyapp.persistence.builder.VertexBuilderFactory;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseReviewFlashcardVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseSelectFlashcardVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseWriteSentenceUsingWordVertexFactory;
import com.explik.diybirdyapp.persistence.vertexFactory.ExerciseWriteTranslatedSentenceVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.explik.diybirdyapp.TestDataConstants.*;

@Component
public class TestDataProvider {
    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private VertexBuilderFactory builderFactory;

    @Autowired
    private ExerciseWriteSentenceUsingWordVertexFactory writeSentenceUsingWordVertexFactory;

    @Autowired
    private ExerciseWriteTranslatedSentenceVertexFactory writeTranslatedSentenceVertexFactory;

    @Autowired
    private ExerciseSelectFlashcardVertexFactory selectFlashcardVertexFactory;

    @Autowired
    private ExerciseReviewFlashcardVertexFactory reviewFlashcardVertexFactory;

    public void resetData() {
        traversalSource.V().drop().iterate();
        setupData();
    }

    public void setupData() {
        addLanguages(traversalSource);
        addContent(traversalSource);
        addExercises(traversalSource);
    }

    private void addLanguages(GraphTraversalSource traversalSource) {
        // Build Danish language
        builderFactory.createLanguageVertexBuilder()
                .withId(Languages.Danish.Id)
                .withName(Languages.Danish.Name)
                .withAbbreviation(Languages.Danish.Abbreviation)
                .withGoogleTextToSpeech(
                        Languages.Danish.TextToSpeech.LanguageCode,
                        Languages.English.TextToSpeech.VoiceName)
                .build(traversalSource);

        // Build English language
        builderFactory.createLanguageVertexBuilder()
                .withId(Languages.English.Id)
                .withName(Languages.English.Name)
                .withAbbreviation(Languages.English.Abbreviation)
                .withGoogleTextToSpeech(
                        Languages.English.TextToSpeech.LanguageCode,
                        Languages.English.TextToSpeech.VoiceName)
                .build(traversalSource);
    }

    private void addContent(GraphTraversalSource traversalSource) {
        addFlashcardContent(traversalSource);
        addFlashcardDeckContent(traversalSource);
    }

    private void addFlashcardContent(GraphTraversalSource traversalSource) {
        var danishLanguage = LanguageVertex.findById(traversalSource, Languages.Danish.Id);
        var englishLanguage = LanguageVertex.findById(traversalSource, Languages.English.Id);

        builderFactory.createFlashcardVertexBuilder()
                .withId(Flashcard.Id)
                .withDefaultLanguages(danishLanguage, englishLanguage)
                .withFrontText(Flashcard.FrontText)
                .withBackText(Flashcard.BackText)
                .build(traversalSource);
    }

    private void addFlashcardDeckContent(GraphTraversalSource traversalSource) {
        builderFactory.createFlashcardDeckVertexBuilder()
                .withId(FlashcardDeck.Id)
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText(FlashcardDeck.Flashcard1.FrontText)
                        .withBackText(FlashcardDeck.Flashcard1.BackText))
                .addFlashcard(new FlashcardVertexBuilder()
                        .withFrontText(FlashcardDeck.Flashcard2.FrontText)
                        .withBackText(FlashcardDeck.Flashcard2.BackText))
                .build(traversalSource);
    }

    private void addExercises(GraphTraversalSource traversalSource) {
        addWriteSentenceUsingWordExercise(traversalSource);
        addWriteTranslatedSentenceExercise(traversalSource);
        addSelectFlashcardExercise(traversalSource);
        addReviewFlashcardExercise(traversalSource);
    }

    private void addWriteSentenceUsingWordExercise(GraphTraversalSource traversalSource) {
        var word = builderFactory.createTextContentVertexBuilder()
                .withValue(WriteSentenceUsingWordExercise.Word)
                .build(traversalSource);

        writeSentenceUsingWordVertexFactory.create(
                traversalSource,
                new ExerciseWriteSentenceUsingWordVertexFactory.Options(
                        WriteSentenceUsingWordExercise.Id,
                        WriteSentenceUsingWordExercise.TargetLanguage,
                        word));
    }

    private void addWriteTranslatedSentenceExercise(GraphTraversalSource traversalSource) {
        var sentence = builderFactory.createTextContentVertexBuilder()
                .withValue(WriteTranslatedSentenceExercise.Sentence)
                .build(traversalSource);

        writeTranslatedSentenceVertexFactory.create(
                traversalSource,
                new ExerciseWriteTranslatedSentenceVertexFactory.Options(
                        WriteTranslatedSentenceExercise.Id,
                        WriteTranslatedSentenceExercise.TargetLanguage,
                        sentence));
    }

    private void addSelectFlashcardExercise(GraphTraversalSource traversalSource) {
        var flashcard1 = builderFactory.createFlashcardVertexBuilder()
                .withFrontText(SelectFlashcardExercise.FlashcardText1)
                .withBackText(SelectFlashcardExercise.FlashcardText1)
                .build(traversalSource);

        var flashcard2 = builderFactory.createFlashcardVertexBuilder()
                .withFrontText(SelectFlashcardExercise.FlashcardText2)
                .withBackText(SelectFlashcardExercise.FlashcardText2)
                .build(traversalSource);

        var flashcard3 = builderFactory.createFlashcardVertexBuilder()
                .withFrontText(SelectFlashcardExercise.FlashcardText3)
                .withBackText(SelectFlashcardExercise.FlashcardText3)
                .build(traversalSource);

        var flashcard4 = builderFactory.createFlashcardVertexBuilder()
                .withFrontText(SelectFlashcardExercise.FlashcardText4)
                .withBackText(SelectFlashcardExercise.FlashcardText4)
                .build(traversalSource);

        selectFlashcardVertexFactory.create(
                traversalSource,
                new ExerciseSelectFlashcardVertexFactory.Options(
                        SelectFlashcardExercise.Id,
                        null,
                        flashcard1,
                        List.of(flashcard2, flashcard3, flashcard4),
                        "front"));
    }

    private void addReviewFlashcardExercise(GraphTraversalSource traversalSource) {
        var flashcard = builderFactory.createFlashcardVertexBuilder()
                .withFrontText(ReviewFlashcardExercise.Flashcard.FrontText)
                .withBackText(ReviewFlashcardExercise.Flashcard.BackText)
                .build(traversalSource);

        reviewFlashcardVertexFactory.create(
                traversalSource,
                new ExerciseReviewFlashcardVertexFactory.Options(
                        ReviewFlashcardExercise.Id,
                        null,
                        flashcard));
    }
}
