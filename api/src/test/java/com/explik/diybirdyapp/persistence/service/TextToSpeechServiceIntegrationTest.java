package com.explik.diybirdyapp.persistence.service;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TextToSpeechServiceIntegrationTest {
    final String filePath = "src/test/resources/test.wav";

    @BeforeEach
    @AfterEach
    public void setupAndTearDown() {
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
    }

    @Autowired
    private TextToSpeechService textToSpeechService;

    @Test
    public void givenValidConfig_whenGenerateAudioFile_createAudioFile() throws IOException {
        var textObject = new TextToSpeechService.Text(
                "Hello, this is a test.",
                "en-US",
                "en-US-Wavenet-D",
                "LINEAR16");

        textToSpeechService.generateAudioFile(textObject, filePath);

        // Check the output file
        File file = new File(filePath);
        assertTrue(file.exists());
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public GraphTraversalSource traversalSource() {
            var graph = TinkerGraph.open();
            return graph.traversal();
        }
    }
}
