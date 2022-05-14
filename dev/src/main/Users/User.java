package main.Users;


import main.ExternalServices.Payment.IPayment;
import main.Publisher.*;
import main.Shopping.Purchase;
import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;

import main.Stores.IStore;

import main.Stores.Product;

import main.Stores.Store;
import main.ExternalServices.Supplying.ISupplying;
import main.utils.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class User implements Observable {

    private boolean isSystemManager;
    private String userName;
    private String hashed_password;
    private AtomicBoolean isLoggedIn;
    private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
    private ShoppingCart cart;
    private List<ShoppingCart> purchaseHistory;

    private Observer observer;


    // stores connections
    private List<IStore> foundedStores;
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
        foundedStores = new LinkedList<>();
        cart = new ShoppingCart(this);
        registerObserver(new Publisher());
    }

    /**
     * This constructor is used once a new user registers to the system
     */
    public User(boolean isSystemManager, String userName, String hashed_password) {
        this.isSystemManager = isSystemManager;
        this.userName = userName;
        this.hashed_password = hashed_password;
        isLoggedIn = new AtomicBoolean(false);
        foundedStores = new LinkedList<>();
        cart = new ShoppingCart(this);
        ownedStores = new LinkedList<>();
        managedStores = new LinkedList<>();
        messages=new ConcurrentLinkedQueue<>();
		securityQNA = new LinkedList<>();
        purchaseHistory = new LinkedList<>();
        registerObserver(new Publisher());
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

    public Observer getObserver(){
        return observer;
    }

    public void LogIn() {
        this.isLoggedIn.set(true);
        notifyObserver();
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
        if (foundedStores.contains(IStore)) {
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
        if (user.foundedStores.contains(IStore))
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

    public boolean ShouldBeNotfiedForBargaining(IStore store){
        return hasPermission(store, StorePermission.BargainPermission);
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


    public boolean closeStore(IStore IStore) {
        if (!foundedStores.contains(IStore))
            throw new IllegalArgumentException("You're not the founder of the store!");
        IStore.closeStore();
        return true;
    }

    public boolean reOpenStore(IStore IStore) {
        if (!foundedStores.contains(IStore))
            throw new IllegalArgumentException("You're not the founder of the store!");
        IStore.reOpen();
        return true;
    }


    public HashMap<User, String> getStoreStaff(IStore IStore) {
        if (hasPermission(IStore, StorePermission.OwnerPermission))
            return IStore.getStoreStaff();
        throw new IllegalArgumentException("You don't have permission to do that");
    }

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
        observer.setWebSocket(null);
    }

    public void purchaseCart(PaymentInformation pinfo, SupplyingInformation sinfo, IPayment psystem, ISupplying ssystem) throws Exception{
        Purchase p = new Purchase(pinfo, sinfo, this, this.cart, psystem, ssystem);
        p.executePurchase();
        this.resetCart();
    }

    public List<ShoppingCart> getPurchaseHistory() {
        return this.purchaseHistory;
    }

    public void resetCart(){
        this.cart = new ShoppingCart(this);
    }

    public void addCartToHistory(ShoppingCart cart){
        this.purchaseHistory.add(cart);
    }

    public void setStoreFounder(IStore IStore) throws Exception
    {
        if(!this.foundedStores.isEmpty())
        {
            throw new Exception("There is already a store founder");
        }
        this.foundedStores.add(IStore);
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

    public IStore getStoreInPurchaseHistory(String storeName) {
        for(ShoppingCart sc : purchaseHistory)
        {
            if(sc.isStoreInCart(storeName))
            {
                return sc.getStore(storeName);
            }
        }
        return null;
    }

    public List<String> receiveQuestionsFromStore(IStore store) {
        if (hasPermission(store, StorePermission.AnswerAndTakeRequests))
            return store.getStoreMessages();
        throw new IllegalArgumentException("You don't have permission to do that");
    }


    public boolean sendRespondFromStore(IStore IStore, User toRespond, String msg) {
        if (hasPermission(IStore, StorePermission.AnswerAndTakeRequests))
            return IStore.respondToBuyer(toRespond, msg);
        throw new IllegalArgumentException("You don't have permission to do that");
    }

    public ConcurrentHashMap<ShoppingBasket, LocalDateTime> getStorePurchaseHistoryByTime(IStore IStore) {
        if (isSystemManager || hasPermission(IStore, StorePermission.ViewStoreHistory))
            return IStore.getPurchaseHistoryByTime();
        throw new IllegalArgumentException("The user doesn't have permissions to do that!");
    }
    public ConcurrentHashMap<ShoppingBasket, User> getStorePurchaseHistoryByUser(IStore IStore) {
        if (isSystemManager || hasPermission(IStore, StorePermission.ViewStoreHistory))
            return IStore.getPurchaseHistoryByUser();
        throw new IllegalArgumentException("The user doesn't have permissions to do that!");
    }

    public boolean removeStore(IStore IStore) {
        if (!isSystemManager)
            throw new IllegalArgumentException("You're not a system manager!");

        IStore.CancelStaffRoles();
        return true;
    }

    public void removeFounderRole(IStore IStore) {
        foundedStores.remove(IStore);
    }

    public void removeOwnerRole(OwnerPermissions ownerPermissions) {
        ownedStores.remove(ownerPermissions);
    }

    public void removeManagerRole(ManagerPermissions managerPermissions) {
        managedStores.remove(managerPermissions);
    }

    public List<IStore> deleteUser(User toDelete) {
        if (!isSystemManager)
            throw new IllegalArgumentException("You're not a system manager!");

        List<IStore> deletedStores=new LinkedList<>();

        //removing all the stores that the user has founded
        for (IStore IStore : toDelete.foundedStores) {
            if(removeStore(IStore))
                deletedStores.add(IStore);
        }

        for (OwnerPermissions ownerPermissions : ownedStores) {
            ownerPermissions.getAppointedBy().removeOwnerAppointment(ownerPermissions.getStore(), this);
        }

        for (ManagerPermissions managerPermissions : managedStores) {
            managerPermissions.getAppointedBy().removeManagerAppointment(managerPermissions.getStore(), this);
        }

        return deletedStores;
    }

    public boolean isAdmin() {
        return isSystemManager;
    }

    public IStore openStore(String storeName) {
        IStore IStore = new Store(storeName,this);
        foundedStores.add(IStore);
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

    public boolean addProductToCart(IStore st, String productName, double price) {
        return cart.setCostumeProductPrice(st, productName, price,this );
    }

    public boolean RemoveProductFromCart(IStore st, String productName, int quantity) {
        return cart.RemoveProductFromCart(st, productName, quantity);
    }

    public void addDiscountPasswordToBasket(String storeName, String Password){
        cart.getBasket(storeName).addDiscountPassword(Password);
    }


    public List<IStore> getFoundedStores() {
        return foundedStores;
    }

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

    public List<Pair<String, String>> getSecurityQNA() {
        return securityQNA;
    }

    public List<IStore> getAllStoresIsStaff() {
        LinkedList<IStore> stores=new LinkedList<>();
        stores.addAll(getFoundedStores());
        stores.addAll(getOwnedStores());
        stores.addAll(getManagedStores());
        return stores;
    }

    public void addRafflePolicy(IStore store, String productName, Double price) {
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.addRafflePolicy(productName, price);
    }

    public void addAuctionPolicy(IStore store, String productName, Double price, LocalDate Until) {
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.addAuctionPolicy(productName, price, Until);
    }

    public void addNormalPolicy(IStore store, String productName, Double price) {
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.addNormalPolicy(productName, price);
    }

    public void addBargainPolicy(IStore store, String productName, Double originalPrice) {
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.addBargainPolicy(productName, originalPrice);
    }

    public boolean bidOnProduct(IStore store, String productName, Double costumePrice, PaymentInformation paymentInformation, SupplyingInformation supplyingInformation, IPayment psystem, ISupplying ssystem) {
        Bid bid = new Bid(store.getProduct(productName), this, costumePrice, paymentInformation, psystem, supplyingInformation, ssystem);
        return store.bidOnProduct(productName, bid);
    }

    public List<Bid> getUserBids(IStore store, String productName){
        if(!hasPermission(store, StorePermission.BargainPermission))
            throw new IllegalArgumentException("No permission to view other user's bids.");
        return store.getProduct(productName).getUserBids();
    }

    public void ApproveBid(IStore store, String productName, User user) throws Exception {
        if(!hasPermission(store, StorePermission.BargainPermission))
            throw new IllegalArgumentException("No permission to approve other user's bids.");
        store.getProduct(productName).ApproveBid(user, this);
    }

    public void DeclineBid(IStore store, String productName, User user) throws Exception {
        if(!hasPermission(store, StorePermission.BargainPermission))
            throw new IllegalArgumentException("No permission to decline other user's bids.");
        store.getProduct(productName).DeclineBid(user);
    }

    public void CounterOfferBid(IStore store, String productName, User user, Double offer) throws Exception {
        if (!hasPermission(store, StorePermission.BargainPermission))
            throw new IllegalArgumentException("No permission to approve other user's bids.");
        store.getProduct(productName).counterOfferBid(user, offer);
    }

    @Override
    public void registerObserver(Observer observer) {
        this.observer=observer;
    }

    @Override
    public void notifyObserver(Notification notification) {
        observer.update(notification);
    }

    @Override
    public void notifyObserver() {
        observer.update();
    }

    public int CreateSimpleDiscount(IStore store, LocalDate until, Double percent){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateSimpleDiscount(until, percent);
    }
    public int CreateSecretDiscount(IStore store, LocalDate until, Double percent, String secretCode){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateSecretDiscount(until, percent, secretCode);
    }
    public int CreateConditionalDiscount(IStore store, LocalDate until, Double percent, int condID){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateConditionalDiscount(until, percent, condID);
    }
    public int CreateMaximumCompositeDiscount(IStore store, LocalDate until, List<Integer> discounts){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateMaximumCompositeDiscount(until, discounts);
    }
    public int CreatePlusCompositeDiscount(IStore store, LocalDate until, List<Integer> discounts){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreatePlusCompositeDiscount(until, discounts);
    }

    public void SetDiscountToProduct(IStore store, int discountID, String productName){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.SetDiscountToProduct(discountID, productName);
    }
    public void SetDiscountToStore(IStore store, int discountID){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.SetDiscountToStore(discountID);
    }

    public int CreateBasketValueCondition(IStore store, double requiredValue){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateBasketValueCondition(requiredValue);
    }
    public int CreateCategoryAmountCondition(IStore store, String category, int amount){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateCategoryAmountCondition(category, amount);
    }
    public int CreateProductAmountCondition(IStore store, String productName, int amount){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateProductAmountCondition(productName, amount);
    }
    public int CreateLogicalAndCondition(IStore store, List<Integer> conditionIds){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateLogicalAndCondition(conditionIds);
    }
    public int CreateLogicalOrCondition(IStore store, List<Integer> conditionIds){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateLogicalOrCondition(conditionIds);
    }
    public int CreateLogicalXorCondition(IStore store, int id1, int id2){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateLogicalXorCondition(id1, id2);
    }
    public void SetConditionToDiscount(IStore store, int discountId, int ConditionID){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.SetConditionToDiscount(discountId, ConditionID);
    }

    public void SetConditionToStore(IStore store, int ConditionID){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.SetConditionToStore(ConditionID);
    }

}
