package com.explik.diybirdyapp.model.vertex;

import com.explik.diybirdyapp.model.ExerciseFeedback;
import com.syncleus.ferma.AbstractVertexFrame;
import com.syncleus.ferma.annotations.Property;

public abstract class ExerciseFeedbackVertex extends AbstractVertexFrame implements ExerciseFeedback {
    @Property("id")
    public abstract String getId();

    @Property("id")
    public abstract void setId(String id);

    @Property("feedbackType")
    public abstract String getFeedbackType();

    @Property("feedbackType")
    public abstract void setFeedbackType(String exerciseType);
}
