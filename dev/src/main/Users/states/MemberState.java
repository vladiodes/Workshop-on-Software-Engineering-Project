package main.Users.states;

import main.Persistence.DAO;
import main.Security.ISecurity;
import main.Stores.Store;
import main.Users.Qna;
import main.Users.User;
import main.utils.Pair;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Entity
public class MemberState extends UserStates {
    @OneToMany(cascade = CascadeType.ALL)
    private List<Store> foundedStores;
    private AtomicBoolean isLoggedIn;
    private String username;
    private String hashedPassword;

    @Transient
    private List<Qna> securityQNA;

    public MemberState(String userName, String hashed_password) {
        this.username = userName;
        this.hashedPassword = hashed_password;
        foundedStores = new LinkedList<>();
        isLoggedIn = new AtomicBoolean(false);
        securityQNA = new ArrayList<>();
    }

    public MemberState() {

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
    public Store openStore(String storeName, User openingUser) {
        Store IStore = new Store(storeName,openingUser);
        foundedStores.add(IStore);
        return IStore;
    }

    @Override
    public void addSecurityQuestion(String question, String answer) {
        if(question.isBlank() || answer.isBlank())
        {
            throw new IllegalArgumentException("Question or Answer cant be empty");
        }
        Qna qna = new Qna(question,answer);
        this.securityQNA.add(qna);
        DAO.getInstance().persist(qna);
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
    public List<Store> getFoundedStores() {
        return  this.foundedStores;
    }

    @Override
    public List<Qna> getSecurityQNA() {
        return this.securityQNA;

    }

    @Override
    public String getUserName() {
        return this.username;
    }

    @Override
    public boolean isGuest() {
        return false;
    }
}
