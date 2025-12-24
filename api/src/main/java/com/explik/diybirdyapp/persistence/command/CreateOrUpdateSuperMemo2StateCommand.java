package com.explik.diybirdyapp.persistence.command;

public class CreateOrUpdateSuperMemo2StateCommand implements AtomicCommand {
    private String sessionId;
    private String contentId;
    private String rating;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
