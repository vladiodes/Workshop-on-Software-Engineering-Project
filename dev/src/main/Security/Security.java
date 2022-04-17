package main.Security;

import main.Users.User;

public class Security implements ISecurity {
    @Override
    public String hashPassword(String password) {
        return password.concat("HASHED");
    }

}
