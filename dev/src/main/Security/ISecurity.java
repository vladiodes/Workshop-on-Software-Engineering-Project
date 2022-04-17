package main.Security;


public interface ISecurity {
    /***
     *
     * @param password - password to hash.
     * @return hashed password.
     */
    String hashPassword(String password);

}