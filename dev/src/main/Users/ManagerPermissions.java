package main.Users;

import main.Stores.Store;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ManagerPermissions {

    private User appointedToManager;
    private User appointedBy;
    private Store store;
    private ConcurrentLinkedQueue<StorePermission> permissions;

    public ManagerPermissions(){
        permissions=new ConcurrentLinkedQueue<>();
    }

    public Store getStore() {
        return store;
    }


    public boolean hasPermission(StorePermission permission) {
        return permissions.contains(permission);
    }

    public User getAppointedBy() {
        return appointedBy;
    }

    public User getAppointedToManager() {
        return appointedToManager;
    }
}
