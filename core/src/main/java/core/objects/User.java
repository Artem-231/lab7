package core.objects;

import core.enums.Role;

public class User {
    private final String login;
    private final String passwordHash;
    private Role role;

    public User(String login, String passwordHash, Role role) {
        this.login        = login;
        this.passwordHash = passwordHash;
        this.role         = role;
    }

    public String getLogin()       { return login; }
    public String getPasswordHash(){ return passwordHash; }
    public Role   getRole()        { return role; }
}
