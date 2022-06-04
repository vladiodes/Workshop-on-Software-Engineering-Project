package main.Users.states;

import main.Security.ISecurity;
import main.Stores.Store;
import main.Users.Qna;
import main.Users.User;
import main.utils.Pair;

import javax.persistence.*;
import java.util.List;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class UserStates {

    @Id
    @GeneratedValue
    private int id;
    public abstract Boolean getIsLoggedIn();

    public abstract void changePassword(String newPassHashed, ISecurity security_controller, String oldPassword);

    public abstract void changeUsername(String newUsername);

    public abstract Store openStore(String storeName, User openingUser);

    public abstract void addSecurityQuestion(String question, String answer);

    public abstract void login(String password, ISecurity security_controller);

    public abstract void logout();

    public abstract List<Store> getFoundedStores();

    public abstract List<Qna> getSecurityQNA();

    public abstract String getUserName();
}
