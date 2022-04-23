package main.Users;


import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;

import main.Stores.IStore;

import main.Stores.Product;

import main.Stores.Store;
import main.utils.Pair;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class User {

    private boolean isSystemManager;
    private String userName;
    private String hashed_password;
    private AtomicBoolean isLoggedIn;
    private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
    private ShoppingCart cart;
    private List<ShoppingCart> purchaseHistory;


    // stores connections
    private List<IStore> foundedIStores;
    private List<ManagerPermissions> managedStores;
    private List<OwnerPermissions> ownedStores;
    private List<Pair<String,String>> securityQNA;
    private Boolean isGuest;

    public List<IStore> getManagedStores() {
        List<IStore> IStores = new LinkedList<>();
        for (ManagerPermissions permission : managedStores) {
            IStores.add(permission.getStore());
        }
        return IStores;
    }

    public List<IStore> getOwnedStores() {
        List<IStore> IStores = new LinkedList<>();
        for (OwnerPermissions permissions : ownedStores) {
            IStores.add(permissions.getStore());
        }
        return IStores;
    }

    /**
     * This constructor is used once a new guest enters the system
     */

    public User(String guestID) {
        isSystemManager = false;
        userName = "Guest".concat(guestID);
        hashed_password = null;
        isLoggedIn = new AtomicBoolean(false);
        foundedIStores = new LinkedList<>();
        cart = new ShoppingCart();
    }

    /**
     * This constructor is used once a new user registers to the system
     */
    public User(boolean isSystemManager, String userName, String hashed_password) {
        this.isSystemManager = isSystemManager;
        this.userName = userName;
        this.hashed_password = hashed_password;
        isLoggedIn = new AtomicBoolean(false);
        foundedIStores = new LinkedList<>();
        cart = new ShoppingCart();
        ownedStores = new LinkedList<>();
        managedStores = new LinkedList<>();
        messages=new ConcurrentLinkedQueue<>();
		securityQNA = new LinkedList<>();
        purchaseHistory = new LinkedList<>();
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public String getUserName() {
        return userName;
    }

    public String getHashed_password() {
        return hashed_password;
    }

    public void LogIn() {
        this.isLoggedIn.set(true);
    }

    public Boolean getIsLoggedIn() {
        return isLoggedIn.get();
    }


    public boolean addProductToStore(IStore IStore, String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if (hasPermission(IStore, StorePermission.UpdateAddProducts))
            return IStore.addProduct(productName, category, keyWords, description, quantity, price);
        throw new IllegalArgumentException("This user doesn't have permissions to do that!");
    }

    public boolean updateProductToStore(IStore IStore, String oldProductName,String newProductName, String category, List<String> keyWords, String description, int quantity, double price) {
        if (hasPermission(IStore, StorePermission.UpdateAddProducts))
            return IStore.updateProduct(oldProductName,newProductName, category, keyWords, description, quantity, price);
        throw new IllegalArgumentException("This user doesn't have permissions to do that!");
    }

    private boolean hasPermission(IStore IStore, StorePermission permission) {
        if (foundedIStores.contains(IStore)) {
            //founder can do whatever he likes...
            return true;
        }
        if (getOwnedStores().contains(IStore)) {
            //owner can do almost everything
            return true;
        }
        for (ManagerPermissions mp : managedStores) {
            if (mp.getStore() == IStore) {
                return mp.hasPermission(permission);
            }
        }
        return false;
    }

    public boolean appointOwnerToStore(IStore IStore, User user_to_appoint) {

        //first checking preconditions to make the appointment
        appointOwnerPreconditions(IStore, user_to_appoint);

        OwnerPermissions newOwnerAppointment = new OwnerPermissions(user_to_appoint, this, IStore);
        user_to_appoint.addOwnedStore(newOwnerAppointment);
        IStore.addOwnerToStore(newOwnerAppointment);
        return true;
    }

    private void appointOwnerPreconditions(IStore IStore, User user_to_appoint) {
        //first checking if the appointing (this) user can appoint a owner to the store

        if (!hasPermission(IStore, StorePermission.OwnerPermission))
            throw new IllegalArgumentException("This user can't appoint an owner because he's not an owner/founder of the store");
        if (checkIfAlreadyStaff(IStore, user_to_appoint))
            throw new IllegalArgumentException("This user is already a staff of the store");

    }

    private boolean checkIfAlreadyStaff(IStore IStore, User user) {
        //second checking if the user to appoint isn't already an owner/manager/founder of the store
        if (user.getOwnedStores().contains(IStore))
            return true;
        if (user.foundedIStores.contains(IStore))
            return true;
        return user.getManagedStores().contains(IStore);
    }

    private void addOwnedStore(OwnerPermissions newOwnerAppointment) {
        ownedStores.add(newOwnerAppointment);
    }

    /**
     * This is a recursive function - it deletes the owner of a store and all of the
     * managers and owners that were appointed by the user to the store
     *
     * @return true upon success
     */
    public boolean removeOwnerAppointment(IStore IStore, User appointed_user) {

        OwnerPermissions ow = CheckPreConditionsAndFindOwnerAppointment(IStore, appointed_user);

        // now we delete all appointments by appointed_user
        deleteAllAppointedBy(IStore,
                getAllStoreOwnersAppointedBy(appointed_user, IStore)
                , getAllStoreManagersAppointedBy(appointed_user, IStore), appointed_user);



        //finally - deleting the appointment to owner from the appointed_user
        appointed_user.ownedStores.remove(ow);
        IStore.removeOwner(ow);
        return true;
    }

    private void deleteAllAppointedBy(IStore IStore, List<User> ownersAppointedBy, List<User> managersAppointedBy, User appointing_user) {
        for (User owner : ownersAppointedBy) {
            appointing_user.removeOwnerAppointment(IStore, owner);
        }
        for (User manager : managersAppointedBy) {
            appointing_user.removeManagerAppointment(IStore, manager);
        }
    }

    private OwnerPermissions CheckPreConditionsAndFindOwnerAppointment(IStore IStore, User appointed_user) {
        OwnerPermissions ow = null;
        //checking preconditions
        //first checking if this user is an owner of the store
        if (!appointed_user.hasPermission(IStore, StorePermission.OwnerPermission))
            throw new IllegalArgumentException("The appointed user is not an owner of the store");

        //second, checking if this user can remove the appointment - has to be an appointing user
        for (OwnerPermissions appointment : appointed_user.ownedStores) {
            if (appointment.getStore() == IStore) {
                ow = appointment;
                if (appointment.getAppointedBy() != this) {
                    throw new IllegalArgumentException("The user didn't appoint the user to an owner");
                }
            }
        }
        return ow;
    }

    public boolean removeManagerAppointment(IStore IStore, User manager) {
        ManagerPermissions mp = CheckPreConditionsAndFindManagerAppointment(IStore, manager);

        //deleting the appointment to manager from the appointed_user
        manager.managedStores.remove(mp);
        IStore.removeManager(mp);
        return true;

    }

    private ManagerPermissions CheckPreConditionsAndFindManagerAppointment(IStore IStore, User manager) {
        ManagerPermissions mp = null;
        //checking preconditions
        //first checking if the appointed user is a manager of the store
        if (!manager.getManagedStores().contains(IStore))
            throw new IllegalArgumentException("The appointed user is not a manager of the store");

        //second, checking if this user can remove the appointment - has to be an appointing user

        for (ManagerPermissions ma : manager.managedStores) {
            if (ma.getStore() == IStore) {
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
    private List<User> getAllStoreManagersAppointedBy(User AppointedByUser, IStore IStore) {
        LinkedList<User> managersAppointedBy = new LinkedList<>();
        for (ManagerPermissions managerAppointment : IStore.getManagersAppointments()) {
            if (managerAppointment.getAppointedBy() == AppointedByUser)
                managersAppointedBy.add(managerAppointment.getAppointedToManager());
        }
        return managersAppointedBy;
    }

    /**
     * This function returns all users that are owners and were appointed by AppointedByUser
     */
    private List<User> getAllStoreOwnersAppointedBy(User AppointedByUser, IStore IStore) {
        LinkedList<User> ownersAppointedBy = new LinkedList<>();
        for (OwnerPermissions ownerAppointment : IStore.getOwnersAppointments()) {
            if (ownerAppointment.getAppointedBy() == AppointedByUser)
                ownersAppointedBy.add(ownerAppointment.getAppointedToOwner());
        }
        return ownersAppointedBy;
    }

    public boolean appointManagerToStore(IStore IStore, User user_to_appoint) {
        appointManagerPreconditions(IStore, user_to_appoint);

        ManagerPermissions newManagerAppointment = new ManagerPermissions(user_to_appoint, this, IStore);
        user_to_appoint.addManagedStores(newManagerAppointment);
        IStore.addManager(newManagerAppointment);
        return true;
    }

    private void appointManagerPreconditions(IStore IStore, User user_to_appoint) {
        //first checking preconditions for the appointment
        if (!hasPermission(IStore, StorePermission.OwnerPermission)) {
            throw new IllegalArgumentException("This user doesn't have permission to do that");
        }

        //second checking if the user to appoint isn't already an owner/manager/founder of the store
        if (checkIfAlreadyStaff(IStore, user_to_appoint))
            throw new IllegalArgumentException("This user is already a staff of the store!");
    }

    private void addManagedStores(ManagerPermissions newManagerAppointment) {
        managedStores.add(newManagerAppointment);
    }

    /**
     * This function removes/adds (according to the shouldGrant flag)
     * a permission to a manager of the store (should be appointed by this user).
     */
    public boolean grantOrDeletePermission(User manager, IStore IStore, boolean shouldGrant, StorePermission permission) {

        if (!checkIfAlreadyStaff(IStore, this))
            throw new IllegalArgumentException("This user can't grant permissions!");

        if (!manager.getManagedStores().contains(IStore))
            throw new IllegalArgumentException("This user isn't a manager of the store!");

        for (ManagerPermissions mp : manager.managedStores) {
            if (mp.getStore() == IStore && mp.getAppointedBy() == this) {
                if (shouldGrant)
                    mp.addPermission(permission);
                else
                    mp.removePermission(permission);

                return true;
            }
        }
        throw new IllegalArgumentException("The manager wasn't appointed by this user");
    }


    public boolean closeStore(IStore IStore, NotificationBus bus) {
        if (!foundedIStores.contains(IStore))
            throw new IllegalArgumentException("You're not the founder of the store!");
        IStore.closeStore(bus);
        return true;
    }

    public boolean reOpenStore(IStore IStore, NotificationBus bus) {
        if (!foundedIStores.contains(IStore))
            throw new IllegalArgumentException("You're not the founder of the store!");
        IStore.reOpen(bus);
        return true;
    }


    public HashMap<User, String> getStoreStaff(IStore IStore) {
        if (hasPermission(IStore, StorePermission.OwnerPermission))
            return IStore.getStoreStaff();
        throw new IllegalArgumentException("You don't have permission to do that");
    }

    public List<String> receiveQuestionsFromStore(IStore IStore) {
        if (hasPermission(IStore, StorePermission.AnswerAndTakeRequests))
            return IStore.getQuestions();

    public void addSecurityQuestion(String question, String answer) throws Exception
    {
        if(question.isBlank() || answer.isBlank())
        {
            throw new Exception("Question or Answer cant be empty");
        }
        this.securityQNA.add(new Pair<>(question, answer));
    }

    public void logout() {
        this.isLoggedIn.set(false);
    }

    public void purchaseCart() throws Exception{
        ShoppingCart dupCart = deepCopyCart(cart);
        purchaseHistory.add(dupCart);
        ConcurrentHashMap<String, ShoppingBasket>  baskets = cart.getBaskets();
        for(ShoppingBasket sb : baskets.values())
        {
            sb.purchaseBasket();
        }
        this.cart = new ShoppingCart(); //User's cart is now a new empty cart since the last cart was purchased
    }

    private ShoppingCart deepCopyCart(ShoppingCart cart) {
        return new ShoppingCart(cart);
    }

    public List<ShoppingCart> getPurchaseHistory() {
        return this.purchaseHistory;
    }

    public void setStoreFounder(Store store) throws Exception
    {
        if(!this.foundedStores.isEmpty())
        {
            throw new Exception("There is already a store founder");
        }
        this.foundedStores.add(store);
    }

    public Product findProductInHistoryByNameAndStore(String productName, String storeName) {
        for(ShoppingCart sc : purchaseHistory)
        {
            if(sc.isProductInCart(productName, storeName)) // Only true if product is in the user's purchase history for that specific store
            {
                return sc.getProduct(productName, storeName);
            }
        }
        return null;
    }

    public Store getStoreInPurchaseHistory(String storeName) {
        for(ShoppingCart sc : purchaseHistory)
        {
            if(sc.isStoreInCart(storeName))
            {
                return sc.getStore(storeName);
            }
        }
        return null;
    }
	
    public HashMap<User, String> getStoreStaff(Store store) {
        if (hasPermission(store, StorePermission.OwnerPermission))
            return store.getStoreStaff();
        throw new IllegalArgumentException("You don't have permission to do that");
    }

    public List<Pair<String, String>> receiveQuestionsFromStore(Store store) {
        if (hasPermission(store, StorePermission.AnswerAndTakeRequests))
            return store.getQuestions();
        throw new IllegalArgumentException("You don't have permission to do that");
    }


    public boolean sendRespondFromStore(IStore IStore, User toRespond, String msg, NotificationBus bus) {
        if (hasPermission(IStore, StorePermission.AnswerAndTakeRequests))
            return IStore.respondToBuyer(toRespond, msg, bus);
        throw new IllegalArgumentException("You don't have permission to do that");
    }

    public ConcurrentHashMap<ShoppingBasket, LocalDateTime> getStorePurchaseHistory(IStore IStore) {
        if (isSystemManager || hasPermission(IStore, StorePermission.ViewStoreHistory))
            return IStore.getPurchaseHistory();
        throw new IllegalArgumentException("The user doesn't have permissions to do that!");
    }

    public boolean removeStore(IStore IStore) {
        if (!isSystemManager)
            throw new IllegalArgumentException("You're not a system manager!");

        IStore.CancelStaffRoles();
        return true;
    }

    public void removeFounderRole(IStore IStore) {
        foundedIStores.remove(IStore);
    }

    public void removeOwnerRole(OwnerPermissions ownerPermissions) {
        ownedStores.remove(ownerPermissions);
    }

    public void removeManagerRole(ManagerPermissions managerPermissions) {
        managedStores.remove(managerPermissions);
    }

    public boolean deleteUser(User toDelete) {
        if (!isSystemManager)
            throw new IllegalArgumentException("You're not a system manager!");

        //removing all the stores that the user has founded
        for (IStore IStore : toDelete.foundedIStores) {
            removeStore(IStore);
        }

        for (OwnerPermissions ownerPermissions : ownedStores) {
            ownerPermissions.getAppointedBy().removeOwnerAppointment(ownerPermissions.getStore(), this);
        }

        for (ManagerPermissions managerPermissions : managedStores) {
            managerPermissions.getAppointedBy().removeManagerAppointment(managerPermissions.getStore(), this);
        }

        return true;
    }

    public boolean isAdmin() {
        return isSystemManager;
    }

    public IStore openStore(String storeName) {
        IStore IStore = new Store(storeName,this);
        foundedIStores.add(IStore);
        return IStore;
    }

    public boolean removeProductFromStore(String productName, IStore IStore) {
        if(!hasPermission(IStore,StorePermission.UpdateAddProducts))
            throw new IllegalArgumentException("You don't have permissions to do that");
        return IStore.removeProduct(productName);
    }

    public boolean addProductToCart(IStore st, String productName, int quantity) {
        return cart.addProductToCart(st, productName, quantity);
    }

    public boolean RemoveProductFromCart(IStore st, String productName, int quantity) {
        return cart.RemoveProductFromCart(st, productName, quantity);
    }


    public List<IStore> getFoundedIStores() {
        return foundedIStores;

    public void changePassword(String newPassHashed) {
        this.hashed_password = newPassHashed;
    }

    public void changeUsername(String newUsername) throws Exception{
        if(newUsername.isBlank())
        {
            throw new Exception("Username cant be blank");
        }
        this.userName = newUsername;

    }
}
