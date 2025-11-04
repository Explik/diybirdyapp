package com.explik.diybirdyapp.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String roleName) {
        super("Role not found: " + roleName);
    }

    public static RoleNotFoundException createFromName(String roleName) {
        return new RoleNotFoundException(roleName);
    }
}
