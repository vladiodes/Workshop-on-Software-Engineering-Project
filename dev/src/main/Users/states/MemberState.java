package main.Users.states;

import main.Security.ISecurity;
import main.Stores.IStore;
import main.Stores.Store;
import main.Users.User;
import main.utils.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MemberState implements UserStates {

    private List<IStore> foundedStores;
    private AtomicBoolean isLoggedIn;
    private String username;
    private String hashedPassword;
    private List<Pair<String, String>> securityQNA;

    public MemberState(String userName, String hashed_password) {
        this.username = userName;
        this.hashedPassword = hashed_password;
        foundedStores = new LinkedList<>();
        isLoggedIn = new AtomicBoolean(false);
        securityQNA = new ArrayList<>();
    }


    @Override
    public Boolean getIsLoggedIn() {
        return this.isLoggedIn.get();
    }

    @Override
    public void changePassword(String newPassword, ISecurity security_controller, String oldPassword) {
        if (!security_controller.isValidPassword(newPassword, this.username)) {
            throw new IllegalArgumentException("password is not secure enough.");
        }
        String oldPassHashed = security_controller.hashPassword(oldPassword);
        if(!oldPassHashed.equals(this.hashedPassword))
        {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        this.hashedPassword = security_controller.hashPassword(newPassword);
    }

    @Override
    public void changeUsername(String newUsername) {
        if(newUsername.isBlank())
            throw new IllegalArgumentException("User name can't be blank.");
        username = newUsername;
    }

    @Override
    public IStore openStore(String storeName, User openingUser) {
        IStore IStore = new Store(storeName,openingUser);
        foundedStores.add(IStore);
        return IStore;
    }

    @Override
    public void addSecurityQuestion(String question, String answer) {
        if(question.isBlank() || answer.isBlank())
        {
            throw new IllegalArgumentException("Question or Answer cant be empty");
        }
        this.securityQNA.add(new Pair<>(question, answer));
    }

    @Override
    public void login(String password, ISecurity security_controller) {
        if(this.isLoggedIn.get())
            throw new IllegalArgumentException("user already logged in.");
        if (!this.hashedPassword.equals(security_controller.hashPassword(password))) {
            throw new IllegalArgumentException("Incorrect password.");
        }
        this.isLoggedIn.set(true);
    }

    @Override
    public void logout() {
        if(!isLoggedIn.get())
            throw new IllegalArgumentException("User already logged out.");
        this.isLoggedIn.set(false);
    }

    @Override
    public List<IStore> getFoundedStores() {
        return  this.foundedStores;
    }

    @Override
    public List<Pair<String, String>> getSecurityQNA() {
        return this.securityQNA;

    }

    @Override
    public String getUserName() {
        return this.username;
    }
}
