package main.Users;

import main.Stores.Store;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

public class OwnerPermissions implements Serializable {

    private User appointedToOwner;

    private User appointedBy;

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
