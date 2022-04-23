package main.Users;

import main.Stores.IStore;

public class OwnerPermissions {
    private User appointedToOwner;
    private User appointedBy;
    private IStore IStore;

    public OwnerPermissions(User appointedToOwner, User appointedBy, IStore IStore){
        this.appointedToOwner=appointedToOwner;
        this.appointedBy=appointedBy;
        this.IStore = IStore;
    }


    public IStore getStore() {
        return IStore;
    }

    public User getAppointedBy(){
        return appointedBy;
    }

    public User getAppointedToOwner(){
        return appointedToOwner;
    }
}
