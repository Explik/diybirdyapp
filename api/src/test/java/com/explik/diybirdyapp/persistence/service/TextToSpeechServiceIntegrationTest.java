package com.explik.diybirdyapp.persistence.service;

import com.explik.diybirdyapp.service.TextToSpeechService;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TextToSpeechServiceIntegrationTest {
    final String filePath = "test.wav";

    @Autowired
    BinaryStorageService storageService;

    @BeforeEach
    @AfterEach
    public void setupAndTearDown() {
        if (storageService.get(filePath) != null)
            storageService.delete(filePath);
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
        assertNotNull(storageService.get(filePath));
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
