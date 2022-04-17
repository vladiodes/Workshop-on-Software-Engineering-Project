package main.DTO;

import main.Users.User;

public class UserDTO {

    private String userName;
    private ShoppingCartDTO cart;
    public UserDTO(User u) {
        this.userName = u.getUserName();
        this.cart = new ShoppingCartDTO(u.getCart());
    }

    public String getUserName() {
        return userName;
    }
}
