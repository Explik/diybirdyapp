package com.explik.diybirdyapp.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class TextToSpeechService {
    @Autowired
    BinaryStorageService storageService;

    public void generateAudioFile(Text textObject, String outputPath) throws IOException {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Build the input text
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(textObject.text())
                    .build();

            // Configure the voice selection
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(textObject.languageCode())
                    .setName(textObject.voiceName())
                    .build();

            // Configure the audio settings
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.valueOf(textObject.audioEncoding))
                    .build();

            // Perform the text-to-speech request
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio content from the response
            ByteString audioContents = response.getAudioContent();

            // Write the audio content to the output file
            storageService.set(outputPath, audioContents.toByteArray());
        }
    }

    public record Text (String text, String languageCode, String voiceName, String audioEncoding) { }
}

