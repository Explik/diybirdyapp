package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.internal.GoogleTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.MicrosoftTextToSpeechVoiceModel;
import com.explik.diybirdyapp.model.internal.TextToSpeechModel;
import org.springframework.stereotype.Service;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@Service
public class MicrosoftTextToSpeechService {
    public byte[] generateAudio(TextToSpeechModel<MicrosoftTextToSpeechVoiceModel> textToSpeechModel) throws IOException {
        try {
            var voiceModel = textToSpeechModel.getVoice();
            var text = textToSpeechModel.getText();

            SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                    System.getenv("AZURE_SPEECH_KEY"),
                    System.getenv("AZURE_SPEECH_REGION"));
            speechConfig.setSpeechSynthesisVoiceName(voiceModel.getVoiceName());
            speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio16Khz32KBitRateMonoMp3);

            SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);
            SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();

            if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Speech synthesized to speaker for text [" + text + "]");

                AudioDataStream stream = AudioDataStream.fromResult(speechSynthesisResult);
                System.out.print(stream.getStatus());

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];

                long bytesRead;
                while ((bytesRead = stream.readData(buffer)) > 0) {
                    outputStream.write(buffer, 0, (int) bytesRead);
                }

                return outputStream.toByteArray();
            }
            else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
                System.out.println("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                }
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
