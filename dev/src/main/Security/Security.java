package main.Security;

import main.Users.User;

public class Security implements ISecurity {
    @Override
    public String hashPassword(String password) {
        return password.concat("HASHED");
    }

    @Override
    public String unhashPassword(String hashedPassword) {
        return hashedPassword.substring(0, hashedPassword.length() - "HASHED".length());
    }


}
