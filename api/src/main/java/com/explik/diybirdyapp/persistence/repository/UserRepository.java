package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.user.UserModel;
import java.util.Optional;

public interface UserRepository {
    Optional<UserModel> findByEmail(String username);

    void createUser(UserModel model);
    void createUserRole(String roleName);
}
