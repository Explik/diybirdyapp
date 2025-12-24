package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.internal.GoogleTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TextToSpeechService {
    @Autowired
    GoogleTextToSpeechService googleTextToSpeechService;

    public byte[] generateAudio(TextToSpeechModel textToSpeechModel) throws IOException {
        var voiceModel = textToSpeechModel.getVoice();

        if (voiceModel instanceof GoogleTextToSpeechVoiceModel googleVoiceModel)
            return googleTextToSpeechService.generateAudio(textToSpeechModel);

        throw new UnsupportedOperationException("Text-to-speech voice type not supported");
    }

    public void generateAudioFile(TextToSpeechModel textObject, String outputPath) throws IOException {
        var voiceModel = textObject.getVoice();

        if (voiceModel instanceof GoogleTextToSpeechVoiceModel googleVoiceModel) {
            googleTextToSpeechService.generateAudioFile(textObject, outputPath);
            return;
        }

        throw new UnsupportedOperationException("Text-to-speech voice type not supported");
    }
}
