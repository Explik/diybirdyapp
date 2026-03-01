package com.explik.diybirdyapp.model.exercise;

public class ExerciseSessionProgressDto {
    private String type;
    private int percentage;
    private Integer currentBatch;
    private Boolean hasMoreBatches;
    private Integer reviewNewPercentage;
    private Integer reviewLearningPercentage;
    private Integer reviewLongTermPercentage;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public Integer getCurrentBatch() {
        return currentBatch;
    }

    public void setCurrentBatch(Integer currentBatch) {
        this.currentBatch = currentBatch;
    }

    public Boolean getHasMoreBatches() {
        return hasMoreBatches;
    }

    public void setHasMoreBatches(Boolean hasMoreBatches) {
        this.hasMoreBatches = hasMoreBatches;
    }

    public Integer getReviewNewPercentage() {
        return reviewNewPercentage;
    }

    public void setReviewNewPercentage(Integer reviewNewPercentage) {
        this.reviewNewPercentage = reviewNewPercentage;
    }

    public Integer getReviewLearningPercentage() {
        return reviewLearningPercentage;
    }

    public void setReviewLearningPercentage(Integer reviewLearningPercentage) {
        this.reviewLearningPercentage = reviewLearningPercentage;
    }

    public Integer getReviewLongTermPercentage() {
        return reviewLongTermPercentage;
    }

    public void setReviewLongTermPercentage(Integer reviewLongTermPercentage) {
        this.reviewLongTermPercentage = reviewLongTermPercentage;
    }
}
