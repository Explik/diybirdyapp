package com.explik.diybirdyapp.model.exercise;

public class ExerciseContentVideoDto extends ExerciseContentDto {
    private String videoUrl;

    public ExerciseContentVideoDto() {
        super(TYPE);
    }

    public static String TYPE = "video-content";

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
