package com.explik.diybirdyapp.persistence.command;

public class CreateUserRoleCommand implements AtomicCommand {
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
