package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.exercise.ExerciseSessionModel;
import com.explik.diybirdyapp.persistence.modelFactory.ExerciseSessionModelFactory;
import com.explik.diybirdyapp.persistence.operation.ExerciseSessionOperations;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ExerciseSessionRepositoryUnitTest {
    @MockBean
    ExerciseSessionModelFactory modelFactory;

    @MockBean
    GenericProvider<ExerciseSessionOperations> operationProvider;

    @MockBean(name = "mockedOperations")
    ExerciseSessionOperations operations;

    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    ExerciseSessionRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        traversalSource.V().drop().iterate();
    }

    @Test
    void givenModel_whenAdd_thenReturnModel() {
        var sessionType = "type";
        var model = new ExerciseSessionModel();
        model.setType(sessionType);

        when(operationProvider.get(sessionType)).thenReturn(operations);
        when(operations.init(any(), any())).thenReturn(model);

        var actual = repository.add(model);

        assertEquals(model, actual);
    }

    @Test
    void givenNonExistentExerciseSession_whenGetById_thenThrowsException() {
        var id = "non-existent-id";
        assertThrows(RuntimeException.class, () -> repository.get(id));
    }

    @Test
    void givenExistingExerciseSession_whenGetById_thenReturnExerciseSession() {
        var id = "existent-id";
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setId(id);

        var model = new ExerciseSessionModel();
        when(modelFactory.create(vertex)).thenReturn(model);

        var actual = repository.get(id);

        assertEquals(model, actual);
    }

    @Test
    void givenNonExistentExerciseSession_whenNextExercise_thenThrowsException() {
        var id = "non-existent-id";
        assertThrows(RuntimeException.class, () -> repository.nextExercise(id));
    }

    @Test
    void givenExistingExerciseSession_whenNextExercise_thenReturnUpdatedState() {
        String sessionId = "id", sessionType = "type";
        var vertex = ExerciseSessionVertex.create(traversalSource);
        vertex.setId(sessionId);
        vertex.setType(sessionType);

        var model = new ExerciseSessionModel();
        when(operationProvider.get(sessionType)).thenReturn(operations);
        when(operations.nextExercise(any(), any())).thenReturn(model);

        var actual = repository.nextExercise(sessionId);

        assertEquals(model, actual);
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            return TinkerGraph.open().traversal();
        }
    }
}
