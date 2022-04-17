package main.Security;

import main.Users.User;

public interface ISecurity {
    /***
     *
     * @param password - password to hash.
     * @return hashed password.
     */
    public String hashPassword(String password);
}
