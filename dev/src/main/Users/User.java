package main.Users;

import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Stores.Store;

import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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

        //first checking preconditions to make the appointment
        appointOwnerPreconditions(store, user_to_appoint);

        OwnerPermissions newOwnerAppointment=new OwnerPermissions(user_to_appoint,this,store);
        user_to_appoint.addOwnedStore(newOwnerAppointment);
        store.addOwnerToStore(newOwnerAppointment);
        return true;
    }

    private void appointOwnerPreconditions(Store store, User user_to_appoint) {
        //first checking if the appointing (this) user can appoint a owner to the store
        if(!hasPermission(store,StorePermission.OwnerPermission))
            throw new IllegalArgumentException("This user can't appoint an owner because he's not an owner/founder of the store");
        if(checkIfAlreadyStaff(store, user_to_appoint))
            throw new IllegalArgumentException("This user is already a staff of the store");

    }

    private boolean checkIfAlreadyStaff(Store store, User user) {
        //second checking if the user to appoint isn't already an owner/manager/founder of the store
        if (user.getOwnedStores().contains(store))
            return true;
        if (user.foundedStores.contains(store))
            return true;
        return user.getManagedStores().contains(store);
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

        OwnerPermissions ow = CheckPreConditionsAndFindOwnerAppointment(store, appointed_user);

        // now we delete all appointments by appointed_user
        deleteAllAppointedBy(store,
                getAllStoreOwnersAppointedBy(appointed_user,store)
                , getAllStoreManagersAppointedBy(appointed_user,store),appointed_user);

        //finally - deleting the appointment to owner from the appointed_user
        appointed_user.ownedStores.remove(ow);
        store.removeOwner(ow);
        return true;
    }

    private void deleteAllAppointedBy(Store store, List<User> ownersAppointedBy, List<User> managersAppointedBy,User appointing_user) {
        for(User owner: ownersAppointedBy){
            appointing_user.removeOwnerAppointment(store,owner);
        }
        for(User manager: managersAppointedBy){
            appointing_user.removeManagerAppointment(store,manager);
        }
    }

    private OwnerPermissions CheckPreConditionsAndFindOwnerAppointment(Store store, User appointed_user) {
        OwnerPermissions ow = null;
        //checking preconditions
        //first checking if this user is an owner of the store
        if(!appointed_user.hasPermission(store,StorePermission.OwnerPermission))
            throw new IllegalArgumentException("The appointed user is not an owner of the store");

        //second, checking if this user can remove the appointment - has to be an appointing user
        for (OwnerPermissions appointment : appointed_user.ownedStores) {
            if (appointment.getStore() == store) {
                ow = appointment;
                if (appointment.getAppointedBy() != this) {
                    throw new IllegalArgumentException("The user didn't appoint the user to an owner");
                }
            }
        }
        return ow;
    }

    public boolean removeManagerAppointment(Store store, User manager) {
        ManagerPermissions mp = CheckPreConditionsAndFindManagerAppointment(store, manager);

        //deleting the appointment to manager from the appointed_user
        manager.managedStores.remove(mp);
        store.removeManager(mp);
        return true;
        
    }

    private ManagerPermissions CheckPreConditionsAndFindManagerAppointment(Store store, User manager) {
        ManagerPermissions mp = null;
        //checking preconditions
        //first checking if the appointed user is a manager of the store
        if (!manager.getManagedStores().contains(store))
            throw new IllegalArgumentException("The appointed user is not a manager of the store");

        //second, checking if this user can remove the appointment - has to be an appointing user
        for (ManagerPermissions ma : manager.managedStores) {
            if (ma.getStore() == store) {
                mp = ma;
                if (mp.getAppointedBy() != this)
                    throw new IllegalArgumentException("The user didn't appoint the user to a manager");
            }
        }
        return mp;
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

    public boolean appointManagerToStore(Store store, User user_to_appoint) {
        appointManagerPreconditions(store, user_to_appoint);

        ManagerPermissions newManagerAppointment=new ManagerPermissions(user_to_appoint,this,store);
        user_to_appoint.addManagedStores(newManagerAppointment);
        store.addManager(newManagerAppointment);
        return true;
    }

    private void appointManagerPreconditions(Store store, User user_to_appoint) {
        //first checking preconditions for the appointment
        if(!hasPermission(store,StorePermission.OwnerPermission)) {
            throw new IllegalArgumentException("This user doesn't have permission to do that");
        }

        //second checking if the user to appoint isn't already an owner/manager/founder of the store
        if(checkIfAlreadyStaff(store, user_to_appoint))
            throw new IllegalArgumentException("This user is already a staff of the store!");
    }

    private void addManagedStores(ManagerPermissions newManagerAppointment) {
        managedStores.add(newManagerAppointment);
    }


    /**
     * This function removes/adds (according to the shouldGrant flag)
     * a permission to a manager of the store (should be appointed by this user).
     */
    public boolean grantOrDeletePermission(User manager, Store store,boolean shouldGrant,StorePermission permission) {

        if (!checkIfAlreadyStaff(store, this))
            throw new IllegalArgumentException("This user can't grant permissions!");

        if (!manager.getManagedStores().contains(store))
            throw new IllegalArgumentException("This user isn't a manager of the store!");

        for (ManagerPermissions mp : manager.managedStores) {
            if (mp.getStore() == store && mp.getAppointedBy() == this) {
                if (shouldGrant)
                    mp.addPermission(permission);
                else
                    mp.removePermission(permission);

                return true;
            }
        }
        throw new IllegalArgumentException("The manager wasn't appointed by this user");
    }

    public boolean closeStore(Store store, NotificationBus bus) {
        if(!foundedStores.contains(store))
            throw new IllegalArgumentException("You're not the founder of the store!");
        store.closeStore(bus);
        return true;
    }

    public boolean reOpenStore(Store store,NotificationBus bus) {
        if(!foundedStores.contains(store))
            throw new IllegalArgumentException("You're not the founder of the store!");
        store.reOpen(bus);
        return true;
    }

    public HashMap<User, String> getStoreStaff(Store store) {
        if(hasPermission(store,StorePermission.OwnerPermission))
            return store.getStoreStaff();
        throw new IllegalArgumentException("You don't have permission to do that");
    }

    public String getUserName() {
        return userName;
    }

    public List<String> receiveQuestionsFromStore(Store store) {
        if (hasPermission(store, StorePermission.AnswerAndTakeRequests))
            return store.getQuestions();
        throw new IllegalArgumentException("You don't have permission to do that");
    }


    public boolean sendRespondFromStore(Store store, User toRespond, String msg,NotificationBus bus) {
        if (hasPermission(store, StorePermission.AnswerAndTakeRequests))
            return store.respondToBuyer(toRespond, msg, bus);
        throw new IllegalArgumentException("You don't have permission to do that");
    }


    public ConcurrentHashMap<ShoppingBasket, LocalDateTime> getStorePurchaseHistory(Store store) {
        if(isSystemManager || hasPermission(store,StorePermission.ViewStoreHistory))
            return store.getPurchaseHistory();
        throw new IllegalArgumentException("The user doesn't have permissions to do that!");
    }

    public boolean removeStore(Store store) {
        if(!isSystemManager)
            throw new IllegalArgumentException("You're not a system manager!");

        store.CancelStaffRoles();
        return true;
    }

    public void removeFounderRole(Store store) {
        foundedStores.remove(store);
    }

    public void removeOwnerRole(OwnerPermissions ownerPermissions) {
        ownedStores.remove(ownerPermissions);
    }

    public void removeManagerRole(ManagerPermissions managerPermissions) {
        managedStores.remove(managerPermissions);
    }

    public boolean deleteUser(User toDelete) {
        if(!isSystemManager)
            throw new IllegalArgumentException("You're not a system manager!");

        //removing all the stores that the user has founded
        for(Store store:toDelete.foundedStores){
            removeStore(store);
        }

        for(OwnerPermissions ownerPermissions:ownedStores){
            ownerPermissions.getAppointedBy().removeOwnerAppointment(ownerPermissions.getStore(),this);
        }

        for(ManagerPermissions managerPermissions:managedStores){
            managerPermissions.getAppointedBy().removeManagerAppointment(managerPermissions.getStore(),this);
        }

        return true;
    }

    public boolean isAdmin() {
        return isSystemManager;
    }
}
