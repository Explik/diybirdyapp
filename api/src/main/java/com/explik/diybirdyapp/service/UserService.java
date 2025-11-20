package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.exception.UserAlreadyExistsException;
import com.explik.diybirdyapp.exception.ValidationException;
import com.explik.diybirdyapp.model.user.UserModel;
import com.explik.diybirdyapp.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void createUser(UserModel model) {
        try {
            userRepository.createUser(model);
        } catch (UserAlreadyExistsException e){
            throw ValidationException.fromFieldError("email", "email.alreadyExists");
        }
    }
}
