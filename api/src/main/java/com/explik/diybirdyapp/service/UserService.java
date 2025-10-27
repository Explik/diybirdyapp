package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.model.user.UserModel;
import com.explik.diybirdyapp.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void createUser(UserModel model) {
        userRepository.createUser(model);
    }
}
