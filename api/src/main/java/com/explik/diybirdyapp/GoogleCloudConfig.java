package com.explik.diybirdyapp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class GoogleCloudConfig {
    @Bean
    public TextToSpeechClient textToSpeechClient() throws IOException {
        GoogleCredentials credentials = getGoogleCredentials();
        
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        return TextToSpeechClient.create(settings);
    }

    @Bean
    public SpeechClient speechClient() throws IOException {
        GoogleCredentials credentials = getGoogleCredentials();
        
        SpeechSettings settings = SpeechSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        return SpeechClient.create(settings);
    }

    @Bean
    public Translate translateClient() throws IOException {
        GoogleCredentials credentials = getGoogleCredentials();
        
        TranslateOptions options = TranslateOptions.newBuilder()
                .setCredentials(credentials)
                .build();

        return options.getService();
    }

    /**
     * Get Google Cloud credentials from environment or default credentials chain.
     * @return GoogleCredentials configured for the current environment
     * @throws IOException if credentials cannot be loaded
     */
    private GoogleCredentials getGoogleCredentials() throws IOException {
        String credentialsJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");

        if (credentialsJson != null && !credentialsJson.isEmpty()) {
            // ✅ DigitalOcean / production mode — load from environment secret
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(
                    new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))
            );
            System.out.println("Using service account credentials from environment variable.");
            return credentials;
        } else {
            // ✅ Local dev mode — use default credentials chain
            // This supports `gcloud auth application-default login`
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            System.out.println("Using default application credentials (local environment).");
            return credentials;
        }
    }
}
