package main.Users;

import main.Stores.Store;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
public class OwnerPermissions implements Serializable {
    @Id
    @OneToOne(cascade = CascadeType.ALL)
    private User appointedToOwner;
    @Id
    @OneToOne(cascade = CascadeType.ALL)
    private User appointedBy;
    @Id
    @OneToOne(cascade = CascadeType.ALL)
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
