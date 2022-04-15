package main.Users;

import main.Stores.Store;

import java.security.Permission;
import java.util.List;

public class ManagerPermissions {

    private User appointedToManager;
    private User appointedBy;
    private Store store;
    private List<StorePermission> permissions;
    public Store getStore() {
        return store;
    }


    public boolean hasPermission(StorePermission permission) {
        return permissions.contains(permission);
    }
}
