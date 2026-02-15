package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.internal.GoogleTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.MicrosoftTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import com.explik.diybirdyapp.model.internal.VoiceModel;
import com.explik.diybirdyapp.service.storageService.BinaryStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TextToSpeechService {
    @Autowired
    BinaryStorageService storageService;

    @Autowired
    GoogleTextToSpeechService googleTextToSpeechService;

    @Autowired
    MicrosoftTextToSpeechService microsoftTextToSpeechService;

    @SuppressWarnings("unchecked")
    public byte[] generateAudio(TextToSpeechModel<? extends VoiceModel> textToSpeechModel) throws IOException {
        var voiceModel = textToSpeechModel.getVoice();

        if (voiceModel instanceof GoogleTextToSpeechVoiceModel)
            return googleTextToSpeechService.generateAudio((TextToSpeechModel<GoogleTextToSpeechVoiceModel>) textToSpeechModel);
        if (voiceModel instanceof MicrosoftTextToSpeechVoiceModel)
            return microsoftTextToSpeechService.generateAudio((TextToSpeechModel<MicrosoftTextToSpeechVoiceModel>) textToSpeechModel);

        throw new UnsupportedOperationException("Text-to-speech voice type not supported");
    }

    public void generateAudioFile(TextToSpeechModel<? extends VoiceModel> textObject, String outputPath) throws IOException {
        // Write the audio content to the output file
        var audioBytes = generateAudio(textObject);
        storageService.set(outputPath, audioBytes);
    }
}
