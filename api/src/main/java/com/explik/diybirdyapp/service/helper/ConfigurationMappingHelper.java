package com.explik.diybirdyapp.service.helper;

import com.explik.diybirdyapp.ConfigurationTypes;
import com.explik.diybirdyapp.model.admin.ConfigurationDto;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleSpeechToTextDto;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleTextToSpeechDto;
import com.explik.diybirdyapp.model.admin.ConfigurationGoogleTranslateDto;
import com.explik.diybirdyapp.persistence.vertex.ConfigurationVertex;
import org.springframework.stereotype.Component;

/**
 * Helper class for mapping between configuration DTOs and vertex models.
 * Handles the conversion logic for different configuration types.
 */
@Component
public class ConfigurationMappingHelper {

    /**
     * Creates a configuration DTO from a vertex.
     * @param languageId The language ID (can be null)
     * @param vertex The configuration vertex
     * @return The configuration DTO
     */
    public static ConfigurationDto createConfigModel(String languageId, ConfigurationVertex vertex) {
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH))
            return createGoogleTextToSpeechConfigModel(languageId, vertex);
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_TRANSLATE))
            return createGoogleTranslateConfigModel(languageId, vertex);
        if (vertex.getType().equals(ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT))
            return createGoogleSpeechToTextConfigModel(languageId, vertex);

        throw new IllegalArgumentException("Unsupported configuration type: " + vertex.getType());
    }

    /**
     * Updates a configuration vertex with values from a DTO.
     * @param vertex The configuration vertex to update
     * @param model The configuration DTO with new values
     */
    public static void updateConfigVertex(ConfigurationVertex vertex, ConfigurationDto model) {
        if (model instanceof ConfigurationGoogleTextToSpeechDto googleTextToSpeechModel) {
            updateGoogleTextToSpeechConfigVertex(vertex, googleTextToSpeechModel);
            return;
        }
        if (model instanceof ConfigurationGoogleTranslateDto googleTranslateModel) {
            updateGoogleTranslateConfigVertex(vertex, googleTranslateModel);
            return;
        }
        if (model instanceof ConfigurationGoogleSpeechToTextDto googleSpeechToTextModel) {
            updateGoogleSpeechToTextConfigVertex(vertex, googleSpeechToTextModel);
            return;
        }

        throw new IllegalArgumentException("Unsupported configuration model type: " + model.getClass().getName());
    }

    private static ConfigurationGoogleTextToSpeechDto createGoogleTextToSpeechConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationGoogleTextToSpeechDto();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(vertex.getPropertyValue("languageCode"));
        model.setVoiceName(vertex.getPropertyValue("voiceName"));
        return model;
    }

    private static ConfigurationGoogleSpeechToTextDto createGoogleSpeechToTextConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationGoogleSpeechToTextDto();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(vertex.getPropertyValue("languageCode"));
        return model;
    }

    private static ConfigurationGoogleTranslateDto createGoogleTranslateConfigModel(String languageId, ConfigurationVertex vertex) {
        var model = new ConfigurationGoogleTranslateDto();
        model.setId(vertex.getId());
        model.setLanguageId(languageId);
        model.setLanguageCode(vertex.getPropertyValue("languageCode"));
        return model;
    }

    private static void updateGoogleTextToSpeechConfigVertex(ConfigurationVertex vertex, ConfigurationGoogleTextToSpeechDto model) {
        vertex.setType(ConfigurationTypes.GOOGLE_TEXT_TO_SPEECH);
        vertex.setPropertyValue("languageCode", model.getLanguageCode());
        vertex.setPropertyValue("voiceName", model.getVoiceName());
    }

    private static void updateGoogleSpeechToTextConfigVertex(ConfigurationVertex vertex, ConfigurationGoogleSpeechToTextDto model) {
        vertex.setType(ConfigurationTypes.GOOGLE_SPEECH_TO_TEXT);
        vertex.setPropertyValue("languageCode", model.getLanguageCode());
    }

    private static void updateGoogleTranslateConfigVertex(ConfigurationVertex vertex, ConfigurationGoogleTranslateDto model) {
        vertex.setType(ConfigurationTypes.GOOGLE_TRANSLATE);
        vertex.setPropertyValue("languageCode", model.getLanguageCode());
    }
}
