package main.Security;

import main.Users.User;

public interface ISecurity {
    /***
     *
     * @param password - password to hash.
     * @return hashed password.
     */
    public String hashPassword(String password);

    /***
     *
     * @param hashedPassword - password to unhash
     * @return original password.
     */
    public String unhashPassword(String hashedPassword);
}
