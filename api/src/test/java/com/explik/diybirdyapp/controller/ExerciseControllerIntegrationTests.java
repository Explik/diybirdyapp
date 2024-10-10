package com.explik.diybirdyapp.controller;

import com.explik.diybirdyapp.ExerciseAnswerTypes;
import com.explik.diybirdyapp.ExerciseTypes;
import com.explik.diybirdyapp.controller.dto.ExerciseContentFlashcardDto;
import com.explik.diybirdyapp.controller.dto.ExerciseContentTextDto;
import com.explik.diybirdyapp.controller.dto.ExerciseInputMultipleChoiceTextDto;
import com.explik.diybirdyapp.model.ExerciseAnswerModel;
import com.explik.diybirdyapp.service.DataInitializerService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExerciseControllerIntegrationTests {
    @Autowired
    DataInitializerService dataInitializer;

    @Autowired
    ExerciseController controller;

    // Runs before each test
    @BeforeEach
    void setUp() {
        dataInitializer.resetInitialData();
    }

    @Test
    void givenExistingWriteSentenceUsingWordExercise_whenGetById_thenReturnExercise() {
        // IMPORTANT: Relies on data from DataInitializer.addInitialExerciseData()
        var actual = controller.get("1");

        assertEquals("1", actual.getId());
        assertEquals(ExerciseTypes.WRITE_SENTENCE_USING_WORD, actual.getType());
        assertEquals("example", actual.getProperties().get("word"));
    }

    @Test
    void givenExistingWriteTranslatedSentenceExercise_whenGetById_thenReturnExercise() {
        // IMPORTANT: Relies on data from DataInitializer.addInitialExerciseData()
        var actual = controller.get("2");

        assertEquals("2", actual.getId());
        assertEquals(ExerciseTypes.WRITE_TRANSLATED_SENTENCE, actual.getType());
        assertEquals("Danish", actual.getProperties().get("targetLanguage"));

        var content = (ExerciseContentTextDto)actual.getContent();
        assertEquals("This is an example sentence", content.getText());
    }

    @Test
    void givenExistingMultipleChoiceTextExercise_whenGetById_thenReturnExercise() {
        // IMPORTANT: Relies on data from DataInitializer.addInitialExerciseData()
        var actual = controller.get("3");

        assertEquals("3", actual.getId());
        assertEquals(ExerciseTypes.MULTIPLE_CHOICE_TEXT, actual.getType());

        var input = (ExerciseInputMultipleChoiceTextDto)actual.getInput();
        var options = input.getOptions().stream().sorted(Comparator.comparing(ExerciseInputMultipleChoiceTextDto.Option::getId)).toList();
        assertEquals("Random option 1", options.get(0).getText());
        assertEquals("Random option 2", options.get(1).getText());
        assertEquals("Random option 3", options.get(2).getText());
        assertEquals("Correct option", options.get(3).getText());
    }

    @Test
    void givenExistingReviewFlashcardContentExercise_whenGetById_thenReturnExercise() {
        // IMPORTANT: Relies on data from DataInitializer.addInitialFlashcardAndFlashcardExerciseData()
        var actual = controller.get("4");

        assertEquals("4", actual.getId());
        assertEquals(ExerciseTypes.REVIEW_FLASHCARD, actual.getType());

        var flashcardContent = (ExerciseContentFlashcardDto)actual.getContent();
        var textContent1 = (ExerciseContentTextDto)flashcardContent.getFront();
        var textContent2 = (ExerciseContentTextDto)flashcardContent.getBack();
        assertNotNull(textContent1);
        assertNotNull(textContent2);
    }

    @Test
    void givenExistingExercise_whenSubmitAnswer_thenReturnFeedback() {
        // IMPORTANT: Relies on data from DataInitializer.addInitialFlashcardAndFlashcardExerciseData()
        var answer = new ExerciseAnswerModel();
        answer.setType(ExerciseAnswerTypes.RECOGNIZABILITY_RATING);
        answer.setRating("easy");

        var actual = controller.submitAnswer("4", answer);

        assertNotNull(actual);
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            var graph = TinkerGraph.open();
            return graph.traversal();
        }
    }

    // TODO Replace indirect dependency on DataInitializer with test configuration
}
