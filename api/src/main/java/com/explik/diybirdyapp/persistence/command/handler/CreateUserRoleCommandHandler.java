package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.persistence.command.CreateUserRoleCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.UserRoleVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateUserRoleCommandHandler implements CommandHandler<CreateUserRoleCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateUserRoleCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateUserRoleCommand command) {
        var roleName = command.getRoleName();

        var existingRole = UserRoleVertex.findByName(traversalSource, roleName);
        if (existingRole != null)
            return;

        var roleVertex = UserRoleVertex.create(traversalSource);
        roleVertex.setName(roleName);
    }
}
