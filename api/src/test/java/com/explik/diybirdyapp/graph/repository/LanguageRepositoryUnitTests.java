package com.explik.diybirdyapp.graph.repository;

import com.explik.diybirdyapp.graph.model.FlashcardLanguageModel;
import com.explik.diybirdyapp.graph.vertex.factory.LanguageVertexFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LanguageRepositoryUnitTests {
    private static final String PRE_EXISTENT_ID = "pre-existent-id";
    private static final String PRE_EXISTENT_NAME = "pre-existent-name";
    private static final String PRE_EXISTENT_ABBREVIATION = "pre-existent-abbreviation";

    @Autowired
    LanguageRepository repository;

    @Test
    void givenUniqueLanguage_whenAdd_thenReturnLanguage() {
        var language = new FlashcardLanguageModel();
        language.setId("new-id-1");
        language.setName("new-name-1");
        language.setAbbreviation("new-abbreviation-1");

        var savedLanguage = repository.add(language);

        assertNotNull(savedLanguage.getId());
        assertNotNull(savedLanguage.getName());
        assertNotNull(savedLanguage.getAbbreviation());
    }

    @Test
    void givenSameId_whenAdd_thenThrowsException() {
        var language = new FlashcardLanguageModel();
        language.setId(PRE_EXISTENT_ID);
        language.setName("new-name-2");
        language.setAbbreviation("new-abbreviation-2");

        assertThrows(IllegalArgumentException.class, () -> {
            repository.add(language);
        });
    }

    @Test
    void givenSameName_whenAdd_thenThrowsException() {
        var language = new FlashcardLanguageModel();
        language.setId("new-id-3");
        language.setName(PRE_EXISTENT_NAME);
        language.setAbbreviation("new-abbreviation-3");

        assertThrows(IllegalArgumentException.class, () -> {
            repository.add(language);
        });
    }

    @Test
    void givenSameAbbreviation_whenAdd_thenThrowsException() {
        var language = new FlashcardLanguageModel();
        language.setId("new-id-4");
        language.setName("new-name-4");
        language.setAbbreviation(PRE_EXISTENT_ABBREVIATION);

        assertThrows(IllegalArgumentException.class, () -> {
            repository.add(language);
        });
    }

    @Test
    void givenExistingLanguage_whenGetAll_thenReturnLanguages() {
        var languages = repository.getAll();
        assertNotEquals(0, languages.size());
    }

    @Test
    void givenNewlyCreatedLanguage_whenGetAll_thenReturnListContainingLanguage() {
        var language = new FlashcardLanguageModel();
        language.setId("new-id-5");
        language.setName("new-name-5");
        language.setAbbreviation("new-abbreviation-5");

        repository.add(language);

        var savedLanguages = repository.getAll();
        var savedLanguage = savedLanguages.stream()
                .filter(l -> l.getId().equals(language.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(savedLanguage);
    }

    @TestConfiguration
    static class Configuration {
        @Autowired
        LanguageVertexFactory languageVertexFactory;

        @Bean
        public GraphTraversalSource traversalSource() {
            var graph = TinkerGraph.open();
            var traversal = graph.traversal();

            languageVertexFactory.create(traversal, new LanguageVertexFactory.Options(
                    PRE_EXISTENT_ID,
                    PRE_EXISTENT_NAME,
                    PRE_EXISTENT_ABBREVIATION));

            return traversal;
        }
    }
}
