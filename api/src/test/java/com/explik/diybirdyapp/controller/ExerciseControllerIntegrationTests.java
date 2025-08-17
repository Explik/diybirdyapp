package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.ExerciseInputTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseContentFlashcardDto;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseContentTextDto;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseInputSelectOptionsDto;
import com.explik.diybirdyapp.controller.dto.exercise.ExerciseInputSelectReviewOptionsDto;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;
import static com.explik.diybirdyapp.TestDataConstants.*;

@SpringBootTest
public class ExerciseControllerIntegrationTests {
    @Autowired
    ExerciseController controller;

    // TODO Initialize test data

    @Test
    void givenExistingWriteSentenceUsingWordExercise_whenGetById_thenReturnExercise() {
        var exerciseId = WriteSentenceUsingWordExercise.Id;

        var actual = controller.get(exerciseId);

        assertEquals(exerciseId, actual.getId());
        assertEquals(ExerciseTypes.WRITE_SENTENCE_USING_WORD, actual.getType());
        assertEquals(WriteSentenceUsingWordExercise.Word, actual.getProperties().get("word"));
    }

    @Test
    void givenExistingWriteTranslatedSentenceExercise_whenGetById_thenReturnExercise() {
        var exerciseId = WriteTranslatedSentenceExercise.Id;

        var actual = controller.get(exerciseId);

        assertEquals(exerciseId, actual.getId());
        assertEquals(ExerciseTypes.WRITE_TRANSLATED_SENTENCE, actual.getType());
        assertEquals(WriteTranslatedSentenceExercise.TargetLanguage, actual.getProperties().get("targetLanguage"));

        var content = (ExerciseContentTextDto)actual.getContent();
        assertEquals(WriteTranslatedSentenceExercise.Sentence, content.getText());
    }

    @Test
    void givenExistingMultipleChoiceTextExercise_whenGetById_thenReturnExercise() {
        var exerciseId = SelectFlashcardExercise.Id;

        var actual = controller.get(exerciseId);

        assertEquals(exerciseId, actual.getId());
        assertEquals(ExerciseTypes.SELECT_FLASHCARD, actual.getType());

        var input = (ExerciseInputSelectOptionsDto)actual.getInput();
        var options = input.getOptions();
        //assertEquals(SelectFlashcardExercise.FlashcardText1, options.get(0).getText());
        //assertEquals(SelectFlashcardExercise.FlashcardText2, options.get(1).getText());
        //assertEquals(SelectFlashcardExercise.FlashcardText3, options.get(2).getText());
        //assertEquals(SelectFlashcardExercise.FlashcardText4, options.get(3).getText());
    }

    @Test
    void givenExistingReviewFlashcardContentExercise_whenGetById_thenReturnExercise() {
        var exerciseId = ReviewFlashcardExercise.Id;

        var actual = controller.get(exerciseId);

        assertEquals(exerciseId, actual.getId());
        assertEquals(ExerciseTypes.REVIEW_FLASHCARD, actual.getType());

        var flashcardContent = (ExerciseContentFlashcardDto)actual.getContent();
        var textContent1 = (ExerciseContentTextDto)flashcardContent.getFront();
        var textContent2 = (ExerciseContentTextDto)flashcardContent.getBack();
        assertNotNull(textContent1);
        assertNotNull(textContent2);
    }

    @Test
    void givenExistingExercise_whenSubmitAnswer_thenReturnFeedback() {
        var answer = new ExerciseInputSelectReviewOptionsDto();
        answer.setType(ExerciseInputTypes.RECOGNIZABILITY_RATING);
        answer.setRating("easy");

        var actual = controller.submitAnswer(ReviewFlashcardExercise.Id, answer);

        assertNotNull(actual);
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            return TinkerGraph.open().traversal();
        }
    }
}
