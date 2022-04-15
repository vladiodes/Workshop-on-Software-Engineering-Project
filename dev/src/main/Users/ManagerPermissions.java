package main.Users;

import main.Stores.Store;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ManagerPermissions {

    private User appointedToManager;
    private User appointedBy;
    private Store store;
    private ConcurrentLinkedQueue<StorePermission> permissions;

    public ManagerPermissions(User appointedToManager,User appointedBy,Store store){
        permissions=new ConcurrentLinkedQueue<>();
        this.appointedToManager=appointedToManager;
        this.store=store;
        this.appointedBy=appointedBy;
        addDefaultPermissions();
    }

    private void addDefaultPermissions() {
        permissions.add(StorePermission.AnswerAndTakeRequests);
        permissions.add(StorePermission.ViewStoreHistory);
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

    public void addPermission(StorePermission permission) {
        if(!permissions.contains(permission))
            permissions.add(permission);
    }

    public void removePermission(StorePermission permission) {
        permissions.remove(permission);
    }
}
