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
            //founder can do whatever he likes...
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

    public boolean appointOwnerToStore(Store store, User user_to_appoint) {

        //first checking if the appointing (this) user can appoint an owner to the store
        if(!foundedStores.contains(store) || !getOwnedStores().contains(store))
            throw new IllegalArgumentException("This user can't appoint an owner because he's not an owner/founder of the store");

        //second checking if the user to appoint isn't already an owner/manager/founder of the store
        if(user_to_appoint.getOwnedStores().contains(store))
            throw new IllegalArgumentException("This user is already an owner of the store!");
        if(user_to_appoint.foundedStores.contains(store))
            throw new IllegalArgumentException("This user is already a founder of the store!");
        if(user_to_appoint.getManagedStores().contains(store))
            throw new IllegalArgumentException("This user is already a manager of the store!");

        OwnerPermissions newOwnerAppointment=new OwnerPermissions(user_to_appoint,this,store);
        user_to_appoint.addOwnedStore(newOwnerAppointment);
        return true;
    }

    private void addOwnedStore(OwnerPermissions newOwnerAppointment) {
        ownedStores.add(newOwnerAppointment);
    }

    /**
     * This is a recursive function - it deletes the owner of a store and all of the
     * managers and owners that were appointed by the user to the store
     * @return true upon success
     */
    public boolean removeOwnerAppointment(Store store, User appointed_user) {

        OwnerPermissions ow = CheckPreConditionsAndFindAppointment(store, appointed_user);

        // now we delete all appointments by appointed_user
        deleteAllAppointedBy(store,
                getAllStoreOwnersAppointedBy(appointed_user,store)
                , getAllStoreManagersAppointedBy(appointed_user,store));

        //finally - deleting the appointment to owner from the appointed_user
        appointed_user.ownedStores.remove(ow);
        return true;
    }

    private void deleteAllAppointedBy(Store store, List<User> ownersAppointedBy, List<User> managersAppointedBy) {
        for(User owner: ownersAppointedBy){
            removeOwnerAppointment(store,owner);
        }
        for(User manager: managersAppointedBy){
            removeManagerAppointment(store,manager);
        }
    }

    private OwnerPermissions CheckPreConditionsAndFindAppointment(Store store, User appointed_user) {
        OwnerPermissions ow=null;
        //checking preconditions
        //first checking if the appointed user is an owner of the store
        if(!appointed_user.getOwnedStores().contains(store))
            throw new IllegalArgumentException("The appointed user is not an owner of the store");

        //second, checking if this user can remove the appointment - has to be a founder or an appointing user
        if(!foundedStores.contains(store)){ //he's not a founder

            for(OwnerPermissions appointment: appointed_user.ownedStores){ //or either he didn't appoint the user to an owner
                if(appointment.getStore()== store)
                {
                    ow=appointment;
                    if(appointment.getAppointedBy()!=this){
                        throw new IllegalArgumentException("The user is not a founder or either didn't appoint the user to an owner");
                    }
                }
            }
        }
        return ow;
    }

    private void removeManagerAppointment(Store store, User manager) {
        
    }

    /**
     * This function returns all users that are managers and were appointed by AppointedByUser
     */
    private List<User> getAllStoreManagersAppointedBy(User AppointedByUser, Store store) {
        LinkedList<User> managersAppointedBy=new LinkedList<>();
        for(ManagerPermissions managerAppointment:store.getManagersAppointments()){
            if(managerAppointment.getAppointedBy()==AppointedByUser)
                managersAppointedBy.add(managerAppointment.getAppointedToManager());
        }
        return managersAppointedBy;
    }

    /**
     * This function returns all users that are owners and were appointed by AppointedByUser
     */
    private List<User> getAllStoreOwnersAppointedBy(User AppointedByUser, Store store) {
        LinkedList<User> ownersAppointedBy=new LinkedList<>();
        for(OwnerPermissions ownerAppointment:store.getOwnersAppointments()){
            if(ownerAppointment.getAppointedBy()==AppointedByUser)
                ownersAppointedBy.add(ownerAppointment.getAppointedToOwner());
        }
        return ownersAppointedBy;
    }
}
