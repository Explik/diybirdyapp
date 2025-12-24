package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.user.UserModel;
import com.explik.diybirdyapp.persistence.query.FindUserByEmailQuery;
import com.explik.diybirdyapp.persistence.vertex.UserRoleVertex;
import com.explik.diybirdyapp.persistence.vertex.UserVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FindUserByEmailQueryHandler implements QueryHandler<FindUserByEmailQuery, Optional<UserModel>> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Override
    public Optional<UserModel> handle(FindUserByEmailQuery query) {
        var userVertex = UserVertex.findWithEmail(traversalSource, query.getEmail());
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
}
