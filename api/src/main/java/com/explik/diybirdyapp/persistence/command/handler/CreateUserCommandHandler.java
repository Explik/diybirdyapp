package com.explik.diybirdyapp.persistence.command.handler;

import com.explik.diybirdyapp.exception.RoleNotFoundException;
import com.explik.diybirdyapp.exception.UserAlreadyExistsException;
import com.explik.diybirdyapp.persistence.command.CreateUserCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import com.explik.diybirdyapp.persistence.vertex.UserRoleVertex;
import com.explik.diybirdyapp.persistence.vertex.UserVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand> {
    private final GraphTraversalSource traversalSource;

    public CreateUserCommandHandler(@Autowired GraphTraversalSource traversalSource) {
        this.traversalSource = traversalSource;
    }

    @Override
    public void handle(CreateUserCommand command) {
        var model = command.getUser();

        // Validate user does not already exist
        var existingUser = UserVertex.findWithEmail(traversalSource, model.getEmail());
        if (existingUser != null)
            throw UserAlreadyExistsException.createFromEmail(model.getEmail());

        // Validate roles exist
        var roleVertices = new ArrayList<UserRoleVertex>();
        for (String roleName : model.getRoles()) {
            var roleVertex = UserRoleVertex.findByName(traversalSource, roleName);
            if (roleVertex == null)
                throw RoleNotFoundException.createFromName(roleName);

            roleVertices.add(roleVertex);
        }

        // Create new vertex
        var userVertex = UserVertex.create(traversalSource);
        userVertex.setId(model.getId() != null ? model.getId() : java.util.UUID.randomUUID().toString());
        userVertex.setEmail(model.getEmail());
        userVertex.setPasswordHash(model.getPasswordHash());

        for (var role : roleVertices) {
            userVertex.addRole(role);
        }
    }
}
