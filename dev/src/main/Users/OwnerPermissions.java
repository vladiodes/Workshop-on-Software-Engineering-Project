package main.Users;

import main.Stores.Store;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class OwnerPermissions implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private User appointedToOwner;

    @OneToOne
    private User appointedBy;

    @OneToOne
    private Store IStore;

    public OwnerPermissions(User appointedToOwner, User appointedBy, Store IStore){
        this.appointedToOwner=appointedToOwner;
        this.appointedBy=appointedBy;
        this.IStore = IStore;
    }

    public OwnerPermissions() {

    }


    public Store getStore() {
        return IStore;
    }

    public User getAppointedBy(){
        return appointedBy;
    }

    public User getAppointedToOwner(){
        return appointedToOwner;
    }
}
