package com.explik.diybirdyapp.model.internal;

public class TextToSpeechModel<T extends VoiceModel> {
    private String text;
    private T voice;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public T getVoice() {
        return voice;
    }

    public void setVoice(T voice) {
        this.voice = voice;
    }

    public static <U extends VoiceModel> TextToSpeechModel<U> create(String text, U voice) {
        TextToSpeechModel<U> model = new TextToSpeechModel<>();
        model.setText(text);
        model.setVoice(voice);
        return model;
    }
}
