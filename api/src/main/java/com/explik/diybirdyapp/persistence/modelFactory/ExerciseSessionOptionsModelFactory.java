package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.model.exercise.*;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionOptionsVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionOptionsModelFactory implements ModelFactory<ExerciseSessionOptionsVertex, ExerciseSessionOptionsModel> {
    @Override
    public ExerciseSessionOptionsModel create(ExerciseSessionOptionsVertex optionsVertex) {
        if (optionsVertex == null)
            return null;

        var type = optionsVertex.getType();

        return switch (type) {
            case ExerciseSessionTypes.LEARN_FLASHCARD -> createLearnModel(optionsVertex);
            case ExerciseSessionTypes.REVIEW_FLASHCARD -> createReviewModel(optionsVertex);
            case ExerciseSessionTypes.SELECT_FLASHCARD_DECK ->  createSelectModel(optionsVertex);
            case ExerciseSessionTypes.WRITE_FLASHCARD -> createWriteModel(optionsVertex);
            default -> throw new RuntimeException("Unsupported ExerciseSessionOptionsVertex type: " + type);
        };
    }

    private ExerciseSessionOptionsLearnFlashcardModel createLearnModel(ExerciseSessionOptionsVertex optionsVertex) {
        var model = new ExerciseSessionOptionsLearnFlashcardModel();

        applyCommonProperties(optionsVertex, model);

        model.setAvailableAnswerLanguageIds(getFlashcardLanguageIds(optionsVertex));
        model.setAnswerLanguageIds(getAnswerLanguageIds(optionsVertex));
        model.setRetypeCorrectAnswerEnabled(optionsVertex.getRetypeCorrectAnswer());

        return model;
    }

    private ExerciseSessionOptionsReviewFlashcardsModel createReviewModel(ExerciseSessionOptionsVertex optionsVertex) {
        var model = new ExerciseSessionOptionsReviewFlashcardsModel();

        applyCommonProperties(optionsVertex, model);
        model.setInitialFlashcardLanguageId(optionsVertex.getInitialFlashcardLanguageId());
        model.setAvailableFlashcardLanguageIds(getFlashcardLanguageIds(optionsVertex));

        return model;
    }

    private ExerciseSessionOptionsSelectFlashcardsModel createSelectModel(ExerciseSessionOptionsVertex optionsVertex) {
        var model = new ExerciseSessionOptionsSelectFlashcardsModel();

        applyCommonProperties(optionsVertex, model);
        model.setInitialFlashcardLanguageId(optionsVertex.getInitialFlashcardLanguageId());
        model.setAvailableFlashcardLanguageIds(getFlashcardLanguageIds(optionsVertex));

        return model;
    }

    private ExerciseSessionOptionsWriteFlashcardsModel createWriteModel(ExerciseSessionOptionsVertex optionsVertex) {
        var model = new ExerciseSessionOptionsWriteFlashcardsModel();

        applyCommonProperties(optionsVertex, model);
        model.setAvailableAnswerLanguageIds(getFlashcardLanguageIds(optionsVertex));
        model.setAnswerLanguageId(getAnswerLanguageIds(optionsVertex)[0]);
        model.setRetypeCorrectAnswerEnabled(optionsVertex.getRetypeCorrectAnswer());

        return model;
    }

    private void applyCommonProperties(ExerciseSessionOptionsVertex optionsVertex, ExerciseSessionOptionsModel model) {
        model.setTextToSpeechEnabled(optionsVertex.getTextToSpeechEnabled());
    }

    private String[] getAnswerLanguageIds(ExerciseSessionOptionsVertex optionsVertex) {
        return optionsVertex.getAnswerLanguages().stream()
                .map(LanguageVertex::getId)
                .toArray(String[]::new);
    }

    private String [] getFlashcardLanguageIds(ExerciseSessionOptionsVertex optionsVertex) {
        var sessionVertex = optionsVertex.getSession();
        if (sessionVertex == null)
            throw new RuntimeException("ExerciseSessionOptionsVertex is not linked to an ExerciseSessionVertex");

        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck == null)
            throw new RuntimeException("ExerciseSessionVertex is not linked to a FlashcardDeckVertex");

        return flashcardDeck.getFlashcardLanguageIds();
    }
}
