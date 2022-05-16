package main.Users.states;

import main.Security.ISecurity;
import main.Stores.IStore;
import main.Users.User;
import main.utils.Pair;

import java.util.List;

public interface UserStates {
    public Boolean getIsLoggedIn();

    public void changePassword(String newPassHashed, ISecurity security_controller, String oldPassword);

    public void changeUsername(String newUsername);

    public IStore openStore(String storeName, User openingUser);

    public void addSecurityQuestion(String question, String answer);

    public void login(String password, ISecurity security_controller);

    public void logout();

    List<IStore> getFoundedStores();

    List<Pair<String, String>> getSecurityQNA();

    String getUserName();
}
