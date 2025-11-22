package com.explik.diybirdyapp.dto.exercise;

public class ExerciseContentImageDto extends ExerciseContentDto {
    public ExerciseContentImageDto() {
        super(TYPE);
    }

    private String imageUrl;

    public static String TYPE = "image-content";

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
