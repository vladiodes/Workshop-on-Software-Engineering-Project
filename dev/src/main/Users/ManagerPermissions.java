package main.Users;

import main.Stores.Store;

import java.util.List;

public class ManagerPermissions {

    private User appointedToManager;
    private User appointedBy;
    private Store store;
    private List<String> permissions;
    public Store getStore() {
        return store;
    }

    public boolean hasUpdateProductPermissions() {
        return false;
    }
}
