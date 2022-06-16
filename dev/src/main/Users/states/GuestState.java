package main.Users.states;

import main.Security.ISecurity;
import main.Stores.Store;
import main.Users.Qna;
import main.Users.User;
import main.utils.Pair;

import javax.persistence.Entity;
import java.util.List;

public class GuestState extends UserStates {
    private final String userName;
    public GuestState(String guestID) {
        userName = "Guest" + guestID;
    }

    @Override
    public Boolean getIsLoggedIn() {
        return false;
    }

    @Override
    public void changePassword(String newPassHashed, ISecurity security_controller, String oldPassword) {
        throw new IllegalArgumentException("Guest can't change password.");
    }

    @Override
    public void changeUsername(String newUsername) {
        throw new IllegalArgumentException("can't change user name.");
    }

    @Override
    public Store openStore(String storeName, User openingUser) {
        throw new IllegalArgumentException("Guest can't oepn a store.");
    }

    @Override
    public void addSecurityQuestion(String question, String answer) {
        throw new IllegalArgumentException("Guest can't have security question.");
    }

    @Override
    public void login(String password, ISecurity security_controller) {
        throw new IllegalArgumentException("Can't login into user.");
    }

    @Override
    public void logout() {
        throw new IllegalArgumentException("can't logout out of use.");
    }

    @Override
    public List<Store> getFoundedStores() {
        return null;
    }

    @Override
    public List<Qna> getSecurityQNA() {
        return null;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public boolean isGuest() {
        return true;
    }
}
