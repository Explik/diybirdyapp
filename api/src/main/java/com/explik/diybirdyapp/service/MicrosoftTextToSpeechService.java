package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.internal.GoogleTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.MicrosoftTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MicrosoftTextToSpeechService {
    public byte[] generateAudio(TextToSpeechModel<MicrosoftTextToSpeechVoiceModel> textToSpeechModel) throws IOException {
        var voiceModel = textToSpeechModel.getVoice();

        throw new UnsupportedOperationException("Not implemented yet");
    }
}
