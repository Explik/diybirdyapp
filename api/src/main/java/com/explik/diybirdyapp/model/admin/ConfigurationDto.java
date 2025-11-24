package com.explik.diybirdyapp.model.admin;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConfigurationMicrosoftTextToSpeechDto.class, name = ConfigurationTypes.MICROSOFT_TEXT_TO_SPEECH),
        @JsonSubTypes.Type(value = ConfigurationGoogleTextToSpeechDto.class, name = ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH),
        @JsonSubTypes.Type(value = ConfigurationGoogleSpeechToTextDto.class, name = ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT),
        @JsonSubTypes.Type(value = ConfigurationGoogleTranslateDto.class, name = ConfigurationTypes.GOOGLE_TRANSLATE)
})
public class ConfigurationDto {
    private String id;
    private String type;
    private String languageId;

    public ConfigurationDto(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }
}
