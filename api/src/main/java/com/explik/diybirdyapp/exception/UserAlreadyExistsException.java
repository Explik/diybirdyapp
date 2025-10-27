package com.explik.diybirdyapp.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public static UserAlreadyExistsException createFromEmail(String email) {
        return new UserAlreadyExistsException("User with email " + email + " already exists.");
    }
}
