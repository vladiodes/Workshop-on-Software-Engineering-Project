package main.Users;

import main.Stores.Store;

import java.util.List;

public class OwnerPermissions {
    private User appointedToOwner;
    private User appointedBy;
    private Store store;
    public Store getStore() {
        return store;
    }
}
