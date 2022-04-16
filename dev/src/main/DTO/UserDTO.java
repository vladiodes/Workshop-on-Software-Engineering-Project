package main.DTO;

import main.Users.User;

public class UserDTO {

    private String userName;

    public UserDTO(User u) {
        userName=u.getUserName();
    }
}
