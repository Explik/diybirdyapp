package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.persistence.vertex.ContentVertex;

public class CreateFlashcardVertexCommand implements AtomicCommand {
    private String id;
    private ContentVertex leftContent;
    private ContentVertex rightContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContentVertex getLeftContent() {
        return leftContent;
    }

    public void setLeftContent(ContentVertex leftContent) {
        this.leftContent = leftContent;
    }

    public ContentVertex getRightContent() {
        return rightContent;
    }

    public void setRightContent(ContentVertex rightContent) {
        this.rightContent = rightContent;
    }
}
