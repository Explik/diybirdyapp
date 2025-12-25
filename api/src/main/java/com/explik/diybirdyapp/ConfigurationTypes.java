package com.explik.diybirdyapp;

import java.util.List;

public class ConfigurationTypes {
    public static final String MICROSOFT_TEXT_TO_SPEECH = "microsoft-text-to-speech";
    public static final String GOOGLE_TEXT_TO_SPEECH = "google-text-to-speech";
    public static final String GOOGLE_SPEECH_TO_TEXT = "google-speech-to-text";
    public static final String GOOGLE_TRANSLATE = "google-translate";

    public static List<String> getAllTypes() {
        return List.of(
            MICROSOFT_TEXT_TO_SPEECH,
            GOOGLE_TEXT_TO_SPEECH,
            GOOGLE_SPEECH_TO_TEXT,
            GOOGLE_TRANSLATE
        );
    }
}
