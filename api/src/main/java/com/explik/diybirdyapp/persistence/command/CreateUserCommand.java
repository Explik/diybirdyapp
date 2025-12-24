package com.explik.diybirdyapp.persistence.command;

import com.explik.diybirdyapp.model.user.UserModel;

public class CreateUserCommand implements AtomicCommand {
    private UserModel user;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
