package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.AbstractVertex;

public class CreatePairVertexCommand implements AtomicCommand {
    private String id;
    private AbstractVertex left;
    private AbstractVertex right;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AbstractVertex getLeft() {
        return left;
    }

    public void setLeft(AbstractVertex left) {
        this.left = left;
    }

    public AbstractVertex getRight() {
        return right;
    }

    public void setRight(AbstractVertex right) {
        this.right = right;
    }
}
