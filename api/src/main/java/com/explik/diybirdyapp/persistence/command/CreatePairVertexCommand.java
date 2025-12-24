package com.explik.diybirdyapp.persistence.command;

public class CreatePairVertexCommand implements AtomicCommand {
    private String id;
    private String leftId;
    private String rightId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLeftId() {
        return leftId;
    }

    public void setLeft(String leftId) {
        this.leftId = leftId;
    }

    public String getRightId() {
        return rightId;
    }

    public void setRight(String rightId) {
        this.rightId = rightId;
    }
}
