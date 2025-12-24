package com.explik.diybirdyapp.persistence.command;

public class UpdateFlashcardVertexCommand implements AtomicCommand {
    private String id;
    private String leftContentId;
    private String rightContentId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLeftContentId() {
        return leftContentId;
    }

    public void setLeftContent(String leftContentId) {
        this.leftContentId = leftContentId;
    }

    public String getRightContentId() {
        return rightContentId;
    }

    public void setRightContent(String rightContentId) {
        this.rightContentId = rightContentId;
    }
}
