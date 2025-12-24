package com.explik.diybirdyapp.persistence.command;

public class CreateFlashcardVertexCommand implements AtomicCommand {
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

    public void setLeftContentId(String leftContentId) {
        this.leftContentId = leftContentId;
    }

    public String getRightContentId() {
        return rightContentId;
    }

    public void setRightContentId(String rightContentId) {
        this.rightContentId = rightContentId;
    }
}
