package com.explik.diybirdyapp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class GoogleCloudConfig {

    @Bean
    public TextToSpeechClient textToSpeechClient() throws IOException {
        String credentialsJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");
        GoogleCredentials credentials;

        if (credentialsJson != null && !credentialsJson.isEmpty()) {
            // ✅ DigitalOcean / production mode — load from environment secret
            credentials = ServiceAccountCredentials.fromStream(
                    new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))
            );
            System.out.println("Using service account credentials from environment variable.");
        } else {
            // ✅ Local dev mode — use default credentials chain
            // This supports `gcloud auth application-default login`
            credentials = GoogleCredentials.getApplicationDefault();
            System.out.println("Using default application credentials (local environment).");
        }

        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        return TextToSpeechClient.create(settings);
    }
}
