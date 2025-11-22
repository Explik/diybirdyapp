package com.explik.diybirdyapp.dto.exercise;

import com.explik.diybirdyapp.ExerciseInputTypes;
import jakarta.validation.constraints.NotNull;

public class ExerciseInputRecordVideoDto extends ExerciseInputDto {
    @NotNull(message = "videoUrl.required")
    private String url;

    public ExerciseInputRecordVideoDto() {
        setType(ExerciseInputTypes.RECORD_VIDEO);
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
}
