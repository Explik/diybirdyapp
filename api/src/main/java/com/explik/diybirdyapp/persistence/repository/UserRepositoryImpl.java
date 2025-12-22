package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.exception.RoleNotFoundException;
import com.explik.diybirdyapp.exception.UserAlreadyExistsException;
import com.explik.diybirdyapp.model.user.UserModel;
import com.explik.diybirdyapp.persistence.query.FindUserByEmailQuery;
import com.explik.diybirdyapp.persistence.query.handler.QueryHandler;
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

    @Autowired
    private QueryHandler<FindUserByEmailQuery, Optional<UserModel>> findUserByEmailQueryHandler;

    @Override
    public Optional<UserModel> findByEmail(String username) {
        var query = new FindUserByEmailQuery();
        query.setEmail(username);
        return findUserByEmailQueryHandler.handle(query);
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
