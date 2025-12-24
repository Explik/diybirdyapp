package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.internal.GoogleTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import java.io.IOException;

@Service
public class GoogleTextToSpeechService {
    @Autowired
    TextToSpeechClient textToSpeechClient;

    public byte[] generateAudio(TextToSpeechModel<GoogleTextToSpeechVoiceModel> textToSpeechModel) throws IOException {
        var voiceModel = textToSpeechModel.getVoice();

        // Build the input text
        SynthesisInput input = SynthesisInput.newBuilder()
                .setText(textToSpeechModel.getText())
                .build();

        // Configure the voice selection
        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode(voiceModel.getVoiceLanguageCode())
                .setName(voiceModel.getVoiceName())
                .build();

        // Configure the audio settings
        AudioEncoding audioEncoding = AudioEncoding.valueOf("LINEAR16");
        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(audioEncoding)
                .build();

        // Perform the text-to-speech request
        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

        // Get the audio content from the response
        ByteString audioContents = response.getAudioContent();

        return audioContents.toByteArray();
    }
}

