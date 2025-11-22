package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.dto.exercise.ExerciseDto;
import com.explik.diybirdyapp.dto.exercise.ExerciseInputDto;
import com.explik.diybirdyapp.model.admin.ExerciseAnswerModel;
import com.explik.diybirdyapp.persistence.modelFactory.ModelFactory;
import com.explik.diybirdyapp.persistence.strategy.ExerciseEvaluationStrategy;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ExerciseRepositoryUnitTest {
    @MockBean
    private GenericProvider<ModelFactory<ExerciseVertex, ExerciseModel>> exerciseModelFactoryProvider;

    @MockBean
    private ModelFactory<ExerciseVertex, ExerciseModel> exerciseModelFactory;

    @MockBean
    private GenericProvider<ExerciseEvaluationStrategy> exerciseOperationProvider;

    @MockBean(name = "mockedExerciseOperations")
    private ExerciseEvaluationStrategy exerciseOperations;

    @Autowired
    private GraphTraversalSource traversalSource;

    @Autowired
    private ExerciseRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        traversalSource.V().drop().iterate();
    }

    @Test
    void givenNonExistentExerciseId_whenGet_thenThrowException() {
        var id = "non-existent-id";
        assertThrows(RuntimeException.class, () -> repository.get(id, null));
    }

    @Test
    void givenExistingExercise_whenGet_thenReturnExercise() {
        // Setting up vertex on graph
        String exerciseId = "id", exerciseType = "type";
        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setId(exerciseId);
        vertex.setType(exerciseType);

        // Setting up the exercise factory provider, factory and model
        var model = new ExerciseDto();
        when(exerciseModelFactoryProvider.get(eq(exerciseType))).thenReturn(exerciseModelFactory);
        when(exerciseModelFactory.create(eq(vertex))).thenReturn(model);

        var actualModel = repository.get(exerciseId, null);

        assertEquals(model, actualModel);
    }

    @Test
    void givenExistingExercise_whenGetAll_thenReturnAllExercises() {
        // Setting up vertices on graph
        var vertex1 = ExerciseVertex.create(traversalSource);
        vertex1.setId("id1");

        var vertex2 = ExerciseVertex.create(traversalSource);
        vertex2.setId("id2");

        // Setting up the exercise factory provider, factory and models
        var model1 = new ExerciseModel();
        var model2 = new ExerciseModel();
        when(exerciseModelFactoryProvider.get(any())).thenReturn(exerciseModelFactory);
        when(exerciseModelFactory.create(eq(vertex1))).thenReturn(model1);
        when(exerciseModelFactory.create(eq(vertex2))).thenReturn(model2);

        var actualModels = repository.getAll();

        assertEquals(List.of(model1, model2), actualModels);
    }

    @Test
    void givenNonExistentExerciseId_whenSubmitAnswer_thenThrowException() {
        var id = "non-existent-id";
        var model = new ExerciseAnswerModel();
        model.setExerciseId(id);

        assertThrows(RuntimeException.class, () -> repository.submitAnswer(model));
    }

    @Test
    void givenExistingExercise_whenSubmitAnswer_thenReturnExercise() {
        // Setting up vertex on graph
        String exerciseId = "id", exerciseType = "type";
        var vertex = ExerciseVertex.create(traversalSource);
        vertex.setId(exerciseId);
        vertex.setType(exerciseType);

        // Setting up the exercise factory provider, factory and model
        var model = new ExerciseDto();
        when(exerciseOperationProvider.get(eq(exerciseType))).thenReturn(exerciseOperations);
        when(exerciseOperations.evaluate(any(), any())).thenReturn(model);

        var inputModel = new ExerciseAnswerModel();
        inputModel.setExerciseId(exerciseId);

        var actualModel = repository.submitAnswer(inputModel);

        assertEquals(model, actualModel);
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            return TinkerGraph.open().traversal();
        }
    }
}
