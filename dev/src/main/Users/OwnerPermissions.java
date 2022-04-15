package main.Users;

import main.Stores.Store;

import java.util.LinkedList;
import java.util.List;

public class OwnerPermissions {
    private User appointedToOwner;
    private User appointedBy;
    private Store store;

    public OwnerPermissions(User appointedToOwner,User appointedBy,Store store){
        this.appointedToOwner=appointedToOwner;
        this.appointedBy=appointedBy;
        this.store=store;
    }


    public Store getStore() {
        return store;
    }

    public User getAppointedBy(){
        return appointedBy;
    }

    public User getAppointedToOwner(){
        return appointedToOwner;
    }
}
