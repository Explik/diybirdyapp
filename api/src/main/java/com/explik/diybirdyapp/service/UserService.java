package com.explik.diybirdyapp.service;

import com.explik.diybirdyapp.exception.UserAlreadyExistsException;
import com.explik.diybirdyapp.exception.ValidationException;
import com.explik.diybirdyapp.model.user.UserModel;
import com.explik.diybirdyapp.persistence.command.CreateUserCommand;
import com.explik.diybirdyapp.persistence.command.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    @Autowired
    CommandHandler<CreateUserCommand> createUserCommandHandler;

    public void createUser(UserModel model) {
        try {
            var command = new CreateUserCommand();
            command.setUser(model);
            createUserCommandHandler.handle(command);
        } catch (UserAlreadyExistsException e){
            throw ValidationException.fromFieldError("email", "email.alreadyExists");
        }
    }
}
