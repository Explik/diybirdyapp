package com.explik.diybirdyapp.persistence.command;

public class CreateOrUpdateSimpleSortStateCommand implements AtomicCommand {
    private String sessionId;
    private String contentId;
    private String pile;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }

    public String getPile() { return pile; }
    public void setPile(String pile) { this.pile = pile; }
}
