package main.Users;

import main.Stores.IStore;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ManagerPermissions {

    private User appointedToManager;
    private User appointedBy;
    private IStore IStore;
    private ConcurrentLinkedQueue<StorePermission> permissions;

    public ManagerPermissions(User appointedToManager, User appointedBy, IStore IStore){
        permissions=new ConcurrentLinkedQueue<>();
        this.appointedToManager=appointedToManager;
        this.IStore = IStore;
        this.appointedBy=appointedBy;
        addDefaultPermissions();
    }

    private void addDefaultPermissions() {
        permissions.add(StorePermission.AnswerAndTakeRequests);
        permissions.add(StorePermission.ViewStoreHistory);
    }

    public IStore getStore() {
        return IStore;
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

    public String permissionsToString() {
        StringBuilder builder=new StringBuilder();
        for(StorePermission permission:permissions){
            builder.append(StorePermission.stringOf(permission));
            builder.append(", ");
        }
        return builder.toString();
    }

    public ConcurrentLinkedQueue<StorePermission> getPermissions() {
        return permissions;
    }
}
