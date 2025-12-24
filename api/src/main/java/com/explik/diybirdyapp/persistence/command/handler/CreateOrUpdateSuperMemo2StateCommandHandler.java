package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateOrUpdateSuperMemo2StateCommand;
import com.explik.diybirdyapp.persistence.vertex.ContentVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionStateVertex;
import com.explik.diybirdyapp.persistence.vertex.ExerciseSessionVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrUpdateSuperMemo2StateCommandHandler implements CommandHandler<CreateOrUpdateSuperMemo2StateCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateOrUpdateSuperMemo2StateCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateOrUpdateSuperMemo2StateCommand command) {
        var sessionVertex = ExerciseSessionVertex.findById(traversalSource, command.getSessionId());
        if (sessionVertex == null)
            throw new IllegalArgumentException("Session with ID " + command.getSessionId() + " does not exist");

        var contentVertex = ContentVertex.getById(traversalSource, command.getContentId());
        if (contentVertex == null)
            throw new IllegalArgumentException("Content with ID " + command.getContentId() + " does not exist");

        var stateVertex = ExerciseSessionStateVertex.findBy(traversalSource, "SuperMemo2", contentVertex, sessionVertex);
        if (stateVertex == null) {
            stateVertex = ExerciseSessionStateVertex.create(traversalSource);
            stateVertex.setType("SuperMemo2");
            stateVertex.setContent(contentVertex);
            sessionVertex.addState(stateVertex);

            resetSuperMemo2Data(stateVertex);
            return;
        }

        var q = getQualityOfResponse(command.getRating());
        if (q <= 3) {
            resetSuperMemo2Data(stateVertex);
            return;
        }

        var n = (int)stateVertex.getPropertyValue("n");
        var last_eq = (double)stateVertex.getPropertyValue("e_factor");
        var last_l = (int)stateVertex.getPropertyValue("last_l");

        var new_eq = last_eq + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02));
        new_eq = clamp(new_eq, 1.3, 2.5);

        int new_l = switch (n) {
            case 1 -> 1;
            case 2 -> 6;
            default -> (int) Math.ceil(last_l * new_eq);
        };

        var next_show = System.currentTimeMillis() + (new_l * 86400000L);
        stateVertex.setPropertyValue("e_factor", new_eq);
        stateVertex.setPropertyValue("n", n + 1);
        stateVertex.setPropertyValue("last_l", new_l);
        stateVertex.setPropertyValue("last_show", System.currentTimeMillis());
        stateVertex.setPropertyValue("next_show", next_show);
    }

    private int getQualityOfResponse(String rating) {
        return switch (rating) {
            case "again" -> 2;
            case "hard" -> 3;
            case "good" -> 4;
            case "easy" -> 5;
            default -> 1;
        };
    }

    private void resetSuperMemo2Data(ExerciseSessionStateVertex stateVertex) {
        stateVertex.setPropertyValue("n", 1);
        stateVertex.setPropertyValue("e_factor", 2.5);
        stateVertex.setPropertyValue("last_l", 0);
        stateVertex.setPropertyValue("last_show", System.currentTimeMillis());
        stateVertex.setPropertyValue("next_show", System.currentTimeMillis() + 86400000L); // Next day
    }

    private static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
