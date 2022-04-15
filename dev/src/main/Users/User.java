package main.Users;

import main.Stores.Store;

import javax.naming.NoPermissionException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class User implements IUser {

    private boolean isSystemManager;
    private String userName;
    private String hashed_password;
    private AtomicBoolean isLoggedIn;

    // stores connections
    private List<Store> foundedStores;
    private List<ManagerPermissions> managedStores;
    private List<OwnerPermissions> ownedStores;

    private List<Store> getManagedStores() {
        List<Store> stores = new LinkedList<>();
        for (ManagerPermissions permission : managedStores) {
            stores.add(permission.getStore());
        }
        return stores;
    }

    private List<Store> getOwnedStores() {
        List<Store> stores = new LinkedList<>();
        for (OwnerPermissions permissions : ownedStores) {
            stores.add(permissions.getStore());
        }
        return stores;
    }

    /**
     * This constructor is used once a new guest enters the system
     */
    public User(int guestID){
        isSystemManager=false;
        userName="Guest".concat(String.valueOf(guestID));
        hashed_password=null;
        isLoggedIn=new AtomicBoolean(false);
        foundedStores=new LinkedList<>();
    }

    /**
     * This constructor is used once a new user registers to the system
     */
    public User(boolean isSystemManager,String userName,String hashed_password){
        this.isSystemManager=isSystemManager;
        this.userName=userName;
        this.hashed_password=hashed_password;
        isLoggedIn=new AtomicBoolean(false);
        foundedStores=new LinkedList<>();
    }


    public boolean addProductToStore(Store store, String productName, String category, List<String> keyWords, String description, int quantity, double price) throws NoPermissionException {
        if (hasPermission(store, StorePermission.UpdateAddProducts))
            return store.addProduct(productName, category, keyWords, description, quantity, price);
        throw new NoPermissionException("This user doesn't have permissions to do that!");
    }

    public boolean updateProductToStore(Store store, String productName, String category, List<String> keyWords, String description, int quantity, double price) throws NoPermissionException {
        if(hasPermission(store,StorePermission.UpdateAddProducts))
            store.updateProduct(productName,category,keyWords,description,quantity,price);
        throw new NoPermissionException("This user doesn't have permissions to do that!");
    }

    private boolean hasPermission(Store store,StorePermission permission){
        if(foundedStores.contains(store)){
            //founder can add do whatever he likes...
            return true;
        }
        if(getOwnedStores().contains(store)){
            //owner can do almost everything
            return true;
        }
        for(ManagerPermissions mp:managedStores){
            if(mp.getStore()==store){
                return mp.hasPermission(permission);
            }
        }
        return false;
    }
}
