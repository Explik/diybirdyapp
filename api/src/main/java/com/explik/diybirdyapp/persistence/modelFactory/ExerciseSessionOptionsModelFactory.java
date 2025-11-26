package com.explik.diybirdyapp.persistence.modelFactory;

import com.explik.diybirdyapp.ExerciseSessionTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.model.exercise.*;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionOptionsVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseTypeVertex;
import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSessionOptionsModelFactory implements ModelFactory<ExerciseSessionOptionsVertex, ExerciseSessionOptionsDto> {
    @Override
    public ExerciseSessionOptionsDto create(ExerciseSessionOptionsVertex optionsVertex) {
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

    private ExerciseSessionOptionsLearnFlashcardsDto createLearnModel(ExerciseSessionOptionsVertex optionsVertex) {
        var model = new ExerciseSessionOptionsLearnFlashcardsDto();

        applyCommonProperties(optionsVertex, model);

        model.setAvailableAnswerLanguages(getFlashcardLanguages(optionsVertex));
        model.setAnswerLanguageIds(getAnswerLanguageIds(optionsVertex));
        model.setRetypeCorrectAnswerEnabled(optionsVertex.getRetypeCorrectAnswer());
        model.setAvailableExerciseTypes(getAvailableExerciseTypeOptions());
        model.setExerciseTypesIds(getExerciseTypeIds(optionsVertex));

        return model;
    }

    private ExerciseSessionOptionsReviewFlashcardsDto createReviewModel(ExerciseSessionOptionsVertex optionsVertex) {
        var model = new ExerciseSessionOptionsReviewFlashcardsDto();

        applyCommonProperties(optionsVertex, model);
        model.setInitialFlashcardLanguageId(optionsVertex.getInitialFlashcardLanguageId());
        model.setAvailableFlashcardLanguages(getFlashcardLanguages(optionsVertex));

        return model;
    }

    private ExerciseSessionOptionsSelectFlashcardsDto createSelectModel(ExerciseSessionOptionsVertex optionsVertex) {
        var model = new ExerciseSessionOptionsSelectFlashcardsDto();

        applyCommonProperties(optionsVertex, model);
        model.setInitialFlashcardLanguageId(optionsVertex.getInitialFlashcardLanguageId());
        model.setAvailableFlashcardLanguages(getFlashcardLanguages(optionsVertex));

        return model;
    }

    private ExerciseSessionOptionsWriteFlashcardsDto createWriteModel(ExerciseSessionOptionsVertex optionsVertex) {
        var model = new ExerciseSessionOptionsWriteFlashcardsDto();

        applyCommonProperties(optionsVertex, model);
        model.setAvailableAnswerLanguages(getFlashcardLanguages(optionsVertex));
        model.setAnswerLanguageId(getAnswerLanguageIds(optionsVertex)[0]);
        model.setRetypeCorrectAnswerEnabled(optionsVertex.getRetypeCorrectAnswer());

        return model;
    }

    private void applyCommonProperties(ExerciseSessionOptionsVertex optionsVertex, ExerciseSessionOptionsDto model) {
        model.setTextToSpeechEnabled(optionsVertex.getTextToSpeechEnabled());
    }

    private String[] getAnswerLanguageIds(ExerciseSessionOptionsVertex optionsVertex) {
        return optionsVertex.getAnswerLanguages().stream()
                .map(LanguageVertex::getId)
                .toArray(String[]::new);
    }

    private ExerciseSessionOptionsLanguageOptionDto[] getFlashcardLanguages(ExerciseSessionOptionsVertex optionsVertex) {
        var sessionVertex = optionsVertex.getSession();
        if (sessionVertex == null)
            throw new RuntimeException("ExerciseSessionOptionsVertex is not linked to an ExerciseSessionVertex");

        var flashcardDeck = sessionVertex.getFlashcardDeck();
        if (flashcardDeck == null)
            throw new RuntimeException("ExerciseSessionVertex is not linked to a FlashcardDeckVertex");

        return flashcardDeck.getFlashcardLanguages().stream().map(languageVertex -> {
                var dto = new ExerciseSessionOptionsLanguageOptionDto();
                dto.setId(languageVertex.getId());
                dto.setIsoCode(languageVertex.getIsoCode());
                return dto;
        }).toArray(ExerciseSessionOptionsLanguageOptionDto[]::new);
    }

    private String[] getExerciseTypeIds(ExerciseSessionOptionsVertex optionsVertex) {
        assert optionsVertex.getType().equals(ExerciseSessionTypes.LEARN_FLASHCARD);

        return optionsVertex
                .getExerciseTypes()
                .stream()
                .map(ExerciseTypeVertex::getId)
                .toArray(String[]::new);
    }

    private ExerciseSessionOptionsLearnFlashcardsDto.ExerciseTypeOption[] getAvailableExerciseTypeOptions() {
        return new ExerciseSessionOptionsLearnFlashcardsDto.ExerciseTypeOption[] {
            new ExerciseSessionOptionsLearnFlashcardsDto.ExerciseTypeOption(ExerciseTypes.SELECT_FLASHCARD, "Select Flashcard Deck"),
            new ExerciseSessionOptionsLearnFlashcardsDto.ExerciseTypeOption(ExerciseTypes.WRITE_FLASHCARD, "Write Flashcard"),
            new ExerciseSessionOptionsLearnFlashcardsDto.ExerciseTypeOption(ExerciseTypes.REVIEW_FLASHCARD, "Review Flashcard")
        };
    }
}
