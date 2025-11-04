package com.explik.diybirdyapp.controller.dto.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import jakarta.validation.constraints.NotNull;

public class ExerciseInputRecordAudioDto extends ExerciseInputDto {
    @NotNull(message = "url.required")
    private String url;

    public ExerciseInputRecordAudioDto() {
        setType(ExerciseInputTypes.RECORD_AUDIO);
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
}
