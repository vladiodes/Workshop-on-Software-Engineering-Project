package main.DTO;

import main.Users.User;

public class UserDTO {

    private String userName;
    private ShoppingCartDTO cart;
    private boolean isAdmin;
    public UserDTO(User u) {
        this.userName = u.getUserName();
        this.cart = new ShoppingCartDTO(u.getCart());
        this.isAdmin=u.isAdmin();
    }

    public String getUserName() {
        return userName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
