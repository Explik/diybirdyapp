package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.exception.RoleNotFoundException;
import com.explik.diybirdyapp.exception.UserAlreadyExistsException;
import com.explik.diybirdyapp.model.user.UserModel;
import com.explik.diybirdyapp.persistence.vertex.UserRoleVertex;
import com.explik.diybirdyapp.persistence.vertex.UserVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public Optional<UserModel> findByEmail(String username) {
        var userVertex = UserVertex.findWithEmail(traversalSource, username);
        if (userVertex == null)
            return Optional.empty();

        var userModel = new UserModel();
        userModel.setId(userVertex.getId());
        userModel.setEmail(userVertex.getEmail());
        userModel.setPasswordHash(userVertex.getPasswordHash());

        var userRoles = userVertex.getRoles().stream().map(UserRoleVertex::getName).toList();
        userModel.setRoles(userRoles);

        return Optional.of(userModel);
    }

    @Override
    public void createUser(UserModel model) {
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

    @Override
    public void createUserRole(String roleName) {
        var existingRole = UserRoleVertex.findByName(traversalSource, roleName);
        if (existingRole != null)
            return;

        var roleVertex = UserRoleVertex.create(traversalSource);
        roleVertex.setName(roleName);
    }
}
