package main.Users;

import main.DTO.ShoppingBasketDTO;
import main.DTO.ShoppingCartDTO;
import main.ExternalServices.Payment.IPayment;
import main.Market.Market;
import main.Persistence.DAO;
import main.Publisher.*;
import main.Publisher.Observable;
import main.Publisher.Observer;
import main.Security.ISecurity;
import main.Shopping.Purchase;
import main.Shopping.ShoppingCart;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.OwnerAppointmentRequest;
import main.Stores.Store;
import main.Users.states.GuestState;
import main.Users.states.MemberState;
import main.Users.states.UserStates;
import main.utils.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Entity
public class User implements Observable {

    @Id
    @GeneratedValue
    private int user_id;
    private boolean isSystemManager;

    @OneToOne(cascade = CascadeType.ALL)
    private ShoppingCart cart;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ShoppingCartDTO> purchaseHistory;

    @Transient
    private Observer observer;
    // maps notification to a bool value: true - if was published to user, false - if wasn't

    @ElementCollection
    private Map<Notification,Boolean> notifications;

    @OneToOne(cascade = CascadeType.ALL)
    private UserStates state;


    // stores connections

    @OneToMany
    private List<ManagerPermissions> managedStores;
    @OneToMany
    private List<OwnerPermissions> ownedStores;

    public User() {
    }

    public List<Store> getManagedStores() {
        List<Store> IStores = new LinkedList<>();
        for (ManagerPermissions permission : managedStores) {
            IStores.add(permission.getStore());
        }
        return IStores;
    }

    public List<Store> getOwnedStores() {
        List<Store> IStores = new LinkedList<>();
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
        cart = new ShoppingCart(this);
        state = new GuestState(guestID);
        notifications= Collections.synchronizedMap(new HashMap<>());
        purchaseHistory=new LinkedList<>();
        registerObserver(new OfflinePublisher());
    }

    /**
     * This constructor is used once a new user registers to the system
     */
    public User(boolean isSystemManager, String userName, String hashed_password) {
        this.isSystemManager = isSystemManager;
        cart = new ShoppingCart(this);
        ownedStores = new LinkedList<>();
        managedStores = new LinkedList<>();
        purchaseHistory = new LinkedList<>();
        state = new MemberState(userName, hashed_password);
        notifications=new ConcurrentHashMap<>();
        registerObserver(new OfflinePublisher());
    }

    public void setState(UserStates state) {
        this.state = state;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public String getUserName() {
        return this.state.getUserName();
    }


    public void LogIn(String password, ISecurity security_controller) {
        this.state.login(password, security_controller);
        notifyObserver();
    }

    public Boolean getIsLoggedIn() {
        return this.state.getIsLoggedIn();
    }


    public boolean addProductToStore(Store IStore, String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if (hasPermission(IStore, StorePermission.UpdateAddProducts))
            return IStore.addProduct(productName, category, keyWords, description, quantity, price);
        throw new IllegalArgumentException("This user doesn't have permissions to do that!");
    }

    public boolean updateProductToStore(Store IStore, String oldProductName,String newProductName, String category, List<String> keyWords, String description, int quantity, double price) {
        if (hasPermission(IStore, StorePermission.UpdateAddProducts))
            return IStore.updateProduct(oldProductName,newProductName, category, keyWords, description, quantity, price);
        throw new IllegalArgumentException("This user doesn't have permissions to do that!");
    }

    private boolean hasPermission(Store IStore, StorePermission permission) {
        if (getFoundedStores().contains(IStore)) {
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

    public boolean appointOwnerToStore(Store IStore, User user_to_appoint) {
        synchronized (IStore) {
            //first checking preconditions to make the appointment

            appointOwnerPreconditions(IStore, user_to_appoint);
            OwnerAppointmentRequest request = new OwnerAppointmentRequest(this, user_to_appoint);
            DAO.getInstance().persist(request);
            IStore.addOwnerRequest(request);
        }

        return true;

    }

    private void appointOwnerPreconditions(Store IStore, User user_to_appoint) {
        //first checking if the appointing (this) user can appoint a owner to the store


        if (!hasPermission(IStore, StorePermission.OwnerPermission))
            throw new IllegalArgumentException("This user can't appoint an owner because he's not an owner/founder of the store");
        if(IStore.containsRequestFor(user_to_appoint))
            throw new IllegalArgumentException("There's already a requested appointment for that user");
        if (checkIfAlreadyStaff(IStore, user_to_appoint))
            throw new IllegalArgumentException("This user is already a staff of the store");

    }

    private boolean checkIfAlreadyStaff(Store IStore, User user) {
        //second checking if the user to appoint isn't already an owner/manager/founder of the store
        if (user.getOwnedStores().contains(IStore))
            return true;
        if (user.getFoundedStores().contains(IStore))
            return true;
        return user.getManagedStores().contains(IStore);
    }

    public void addOwnedStore(OwnerPermissions newOwnerAppointment) {
        ownedStores.add(newOwnerAppointment);
        DAO.getInstance().merge(this);
    }

    /**
     * This is a recursive function - it deletes the owner of a store and all of the
     * managers and owners that were appointed by the user to the store
     *
     * @return true upon success
     */
    public boolean removeOwnerAppointment(Store IStore, User appointed_user) {

        OwnerPermissions ow = CheckPreConditionsAndFindOwnerAppointment(IStore, appointed_user);

        // now we delete all appointments by appointed_user
        deleteAllAppointedBy(IStore,
                getAllStoreOwnersAppointedBy(appointed_user, IStore)
                , getAllStoreManagersAppointedBy(appointed_user, IStore), appointed_user);



        //finally - deleting the appointment to owner from the appointed_user
        appointed_user.ownedStores.remove(ow);
        IStore.removeOwner(ow);
        DAO.getInstance().remove(ow);
        DAO.getInstance().merge(appointed_user);
        return true;
    }

    private void deleteAllAppointedBy(Store IStore, List<User> ownersAppointedBy, List<User> managersAppointedBy, User appointing_user) {
        for (User owner : ownersAppointedBy) {
            appointing_user.removeOwnerAppointment(IStore, owner);
        }
        for (User manager : managersAppointedBy) {
            appointing_user.removeManagerAppointment(IStore, manager);
        }
    }

    private OwnerPermissions CheckPreConditionsAndFindOwnerAppointment(Store IStore, User appointed_user) {
        if(IStore.getFounder()==appointed_user)
            throw new IllegalArgumentException("Can't delete the appointment of a founder");

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

    public boolean removeManagerAppointment(Store IStore, User manager) {
        ManagerPermissions mp = CheckPreConditionsAndFindManagerAppointment(IStore, manager);

        //deleting the appointment to manager from the appointed_user
        manager.managedStores.remove(mp);
        IStore.removeManager(mp);
        DAO.getInstance().remove(mp);
        DAO.getInstance().merge(manager);
        return true;

    }

    private ManagerPermissions CheckPreConditionsAndFindManagerAppointment(Store IStore, User manager) {
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
    private List<User> getAllStoreManagersAppointedBy(User AppointedByUser, Store IStore) {
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
    private List<User> getAllStoreOwnersAppointedBy(User AppointedByUser, Store IStore) {
        LinkedList<User> ownersAppointedBy = new LinkedList<>();
        for (OwnerPermissions ownerAppointment : IStore.getOwnersAppointments()) {
            if (ownerAppointment.getAppointedBy() == AppointedByUser)
                ownersAppointedBy.add(ownerAppointment.getAppointedToOwner());
        }
        return ownersAppointedBy;
    }

    public boolean appointManagerToStore(Store IStore, User user_to_appoint) {
        synchronized (IStore) {
            appointManagerPreconditions(IStore, user_to_appoint);

            ManagerPermissions newManagerAppointment = new ManagerPermissions(user_to_appoint, this, IStore);
            DAO.getInstance().persist(newManagerAppointment);
            user_to_appoint.addManagedStores(newManagerAppointment);
            DAO.getInstance().merge(user_to_appoint);
            IStore.addManager(newManagerAppointment);
            DAO.getInstance().merge(IStore);
        }
        return true;
    }

    public boolean ShouldBeNotfiedForBargaining(Store store){
        return hasPermission(store, StorePermission.BargainPermission);
    }

    private void appointManagerPreconditions(Store IStore, User user_to_appoint) {
        //first checking preconditions for the appointment
        if (!hasPermission(IStore, StorePermission.OwnerPermission)) {
            throw new IllegalArgumentException("This user doesn't have permission to do that");
        }

        if(IStore.containsRequestFor(user_to_appoint))
            throw new IllegalArgumentException("There's an owner appointment for that user");

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
    public boolean grantOrDeletePermission(User manager, Store IStore, boolean shouldGrant, StorePermission permission) {

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
                IStore.notifyProductsStaffChange();
                return true;
            }
        }
        throw new IllegalArgumentException("The manager wasn't appointed by this user");
    }


    public boolean closeStore(Store IStore) {
        if (!getFoundedStores().contains(IStore))
            throw new IllegalArgumentException("You're not the founder of the store!");
        IStore.closeStore();
        return true;
    }

    public boolean reOpenStore(Store IStore) {
        if (!getFoundedStores().contains(IStore))
            throw new IllegalArgumentException("You're not the founder of the store!");
        IStore.reOpen();
        return true;
    }


    public HashMap<User, String> getStoreStaff(Store IStore) {
        if (hasPermission(IStore, StorePermission.OwnerPermission) || isAdmin())
            return IStore.getStoreStaff();
        throw new IllegalArgumentException("You don't have permission to do that");
    }

    public void addSecurityQuestion(String question, String answer) throws Exception
    {
        this.state.addSecurityQuestion(question, answer);
    }

    public void logout() {
        this.state.logout();
        observer=new OfflinePublisher();
    }

    public void purchaseCart(PaymentInformation pinfo, SupplyingInformation sinfo, IPayment psystem, ISupplying ssystem) throws Exception{
        Purchase p = new Purchase(pinfo, sinfo, this, this.cart, psystem, ssystem);
        p.executePurchase();
        this.resetCart();
    }

    public List<ShoppingCartDTO> getPurchaseHistory() {
        return this.purchaseHistory;
    }

    public void resetCart(){
        this.cart = new ShoppingCart(this);
        if(!isGuest())
            DAO.getInstance().persist(cart);
    }

    public void addCartToHistory(ShoppingCart cart){
        ShoppingCartDTO historyCart = new ShoppingCartDTO(cart, this,true);
        if(!isGuest())
            DAO.getInstance().persist(historyCart);
        this.purchaseHistory.add(historyCart);
    }

    public void setStoreFounder(Store IStore)
    {
        if(!this.getFoundedStores().isEmpty())
        {
            throw new IllegalArgumentException("There is already a store founder");
        }
        this.getFoundedStores().add(IStore);
    }

    public boolean isProductInHistoryByNameAndStore(String productName, String storeName) {
        for(ShoppingCartDTO sc : purchaseHistory)
        {
            if(sc.isProductInHistory(productName, storeName))  // Only true if product is in the user's purchase history for that specific store
            {
                return true;
            }
        }
        return false;
    }

    public boolean isStoreInHistory(String storeName)
    {
        for(ShoppingCartDTO sc : this.purchaseHistory)
        {
            if(sc.isStoreInHistory(storeName))
                return true;
        }
        return false;
    }

    public List<String> receiveQuestionsFromStore(Store store) {
        if (hasPermission(store, StorePermission.AnswerAndTakeRequests))
            return store.getStoreMessages();
        throw new IllegalArgumentException("You don't have permission to do that");
    }


    public boolean sendRespondFromStore(Store IStore, User toRespond, String msg) {
        if (hasPermission(IStore, StorePermission.AnswerAndTakeRequests))
            return IStore.respondToBuyer(toRespond, msg);
        throw new IllegalArgumentException("You don't have permission to do that");
    }

    public Map<ShoppingBasketDTO, LocalDateTime> getStorePurchaseHistoryByTime(Store IStore) {
        if (isSystemManager || hasPermission(IStore, StorePermission.ViewStoreHistory))
            return IStore.getPurchaseHistoryByTime();
        throw new IllegalArgumentException("The user doesn't have permissions to do that!");
    }

    public boolean removeStore(Store IStore) {
        if (!isSystemManager)
            throw new IllegalArgumentException("You're not a system manager!");

        IStore.CancelStaffRoles();
        DAO.getInstance().remove(IStore);
        return true;
    }

    public void removeFounderRole(Store IStore) {
        getFoundedStores().remove(IStore);
        DAO.getInstance().merge(this);
    }

    public void removeOwnerRole(OwnerPermissions ownerPermissions) {
        ownedStores.remove(ownerPermissions);
        DAO.getInstance().merge(this);
    }

    public void removeManagerRole(ManagerPermissions managerPermissions) {
        managedStores.remove(managerPermissions);
        DAO.getInstance().merge(this);
    }

    public boolean isAdmin() {
        return isSystemManager;
    }

    public Store openStore(String storeName) {
        return this.state.openStore(storeName, this);
    }

    public boolean removeProductFromStore(String productName, Store IStore) {
        if(!hasPermission(IStore,StorePermission.UpdateAddProducts))
            throw new IllegalArgumentException("You don't have permissions to do that");
        return IStore.removeProduct(productName);
    }

    public boolean addProductToCart(Store st, String productName, int quantity) {
        return cart.addProductToCart(st, productName, quantity);
    }

    public boolean addProductToCart(Store st, String productName, double price) {
        return cart.setCostumeProductPrice(st, productName, price,this );
    }

    public boolean RemoveProductFromCart(Store st, String productName, int quantity) {
        return cart.RemoveProductFromCart(st, productName, quantity);
    }

    public void addDiscountPasswordToBasket(String storeName, String Password){
        cart.getBasket(storeName).addDiscountPassword(Password);
    }


    public List<Store> getFoundedStores() {
        return this.state.getFoundedStores();
    }

    public void changePassword(String newPassHashed, ISecurity security_controller, String oldPassword) {
        this.state.changePassword(newPassHashed, security_controller, oldPassword);
    }

    public void changeUsername(String newUsername){
        this.state.changeUsername(newUsername);
    }

    public List<Qna> getSecurityQNA() {
        return this.state.getSecurityQNA();
    }

    public List<Store> getAllStoresIsStaff() {
        LinkedList<Store> stores=new LinkedList<>();
        stores.addAll(getFoundedStores());
        stores.addAll(getOwnedStores());
        stores.addAll(getManagedStores());
        return stores;
    }


    public void addNormalPolicy(Store store, String productName, Double price) {
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.addNormalPolicy(productName, price);
    }

    public void addBargainPolicy(Store store, String productName, Double originalPrice) {
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.addBargainPolicy(productName, originalPrice);
    }

    public boolean bidOnProduct(Store store, String productName, Double costumePrice, PaymentInformation paymentInformation, SupplyingInformation supplyingInformation, IPayment psystem, ISupplying ssystem) {
        DAO.getInstance().persist(paymentInformation);
        DAO.getInstance().persist(supplyingInformation);
        Bid bid = new Bid(store.getProduct(productName), this, costumePrice, paymentInformation, supplyingInformation);
        DAO.getInstance().persist(bid);
        boolean output= store.bidOnProduct(productName, bid);
        DAO.getInstance().merge(store);
        return output;
    }

    public List<Bid> getUserBids(Store store, String productName){
        if(!hasPermission(store, StorePermission.BargainPermission))
            throw new IllegalArgumentException("No permission to view other user's bids.");
        return store.getProduct(productName).getUserBids(this);
    }

    public void ApproveBid(Store store, String productName, User user,IPayment payment,ISupplying supplying) throws Exception {
        if(!hasPermission(store, StorePermission.BargainPermission))
            throw new IllegalArgumentException("No permission to approve other user's bids.");
        store.getProduct(productName).ApproveBid(user, this,payment,supplying);
    }

    public void DeclineBid(Store store, String productName, User user) throws Exception {
        if(!hasPermission(store, StorePermission.BargainPermission))
            throw new IllegalArgumentException("No permission to decline other user's bids.");
        store.getProduct(productName).DeclineBid(user);
    }

    public void CounterOfferBid(Store store, String productName, User user, Double offer) throws Exception {
        if (!hasPermission(store, StorePermission.BargainPermission))
            throw new IllegalArgumentException("No permission to approve other user's bids.");
        store.getProduct(productName).counterOfferBid(user, offer);
    }

    @Override
    public void registerObserver(Observer observer) {
        this.observer=observer;
        notifyObserver();
    }

    public void setObserver(Observer observer){
        this.observer=observer;
    }

    @Override
    public boolean notifyObserver(Notification notification) {
        if (notifications.get(notification)!=null && notifications.get(notification))
            return false; //was already published...
        boolean flag=notifications.putIfAbsent(notification, false)==null;
        if(observer.update(notification))
        {
            if(flag) {
                notifications.put(notification, true);
                DAO.getInstance().merge(this);
                return true;
            }
            return true;
        }
        return false;
        //DAO.getInstance().merge(this);
    }

    @Override
    public void notifyObserver() {
        LinkedList<Notification> published=new LinkedList<>();
        for(Notification notification:notifications.keySet()){
            if(notifyObserver(notification))
                published.add(notification);
        }

        for(Notification n: published){
            notifications.put(n,true);
        }
        if(published.size()>0)
            DAO.getInstance().merge(this);
    }

    public int CreateSimpleDiscount(Store store, LocalDate until, Double percent){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateSimpleDiscount(until, percent);
    }
    public int CreateSecretDiscount(Store store, LocalDate until, Double percent, String secretCode){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateSecretDiscount(until, percent, secretCode);
    }
    public int CreateConditionalDiscount(Store store, LocalDate until, Double percent, int condID){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateConditionalDiscount(until, percent, condID);
    }
    public int CreateMaximumCompositeDiscount(Store store, LocalDate until, List<Integer> discounts){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateMaximumCompositeDiscount(until, discounts);
    }
    public int CreatePlusCompositeDiscount(Store store, LocalDate until, List<Integer> discounts){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreatePlusCompositeDiscount(until, discounts);
    }

    public void SetDiscountToProduct(Store store, int discountID, String productName){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.SetDiscountToProduct(discountID, productName);
    }
    public void SetDiscountToStore(Store store, int discountID){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.SetDiscountToStore(discountID);
    }

    public int CreateBasketValueCondition(Store store, double requiredValue){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateBasketValueCondition(requiredValue);
    }
    public int CreateCategoryAmountCondition(Store store, String category, int amount){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateCategoryAmountCondition(category, amount);
    }
    public int CreateProductAmountCondition(Store store, String productName, int amount){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateProductAmountCondition(productName, amount);
    }
    public int CreateLogicalAndCondition(Store store, List<Integer> conditionIds){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateLogicalAndCondition(conditionIds);
    }
    public int CreateLogicalOrCondition(Store store, List<Integer> conditionIds){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateLogicalOrCondition(conditionIds);
    }
    public int CreateLogicalXorCondition(Store store, int id1, int id2){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        return store.CreateLogicalXorCondition(id1, id2);
    }
    public void SetConditionToDiscount(Store store, int discountId, int ConditionID){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.SetConditionToDiscount(discountId, ConditionID);
    }

    public void SetConditionToStore(Store store, int ConditionID){
        if(!hasPermission(store, StorePermission.PolicyPermission))
            throw new IllegalArgumentException("You don't have permission to add policies to this store.");
        store.SetConditionToStore(ConditionID);
    }

    public boolean isManager() {
        return (!getManagedStores().isEmpty());
    }

    public boolean isFounder()
    {
        List<Store> foundedStores = getFoundedStores();
        if(foundedStores == null)
            return false;
        return (!getFoundedStores().isEmpty());
    }

    public boolean isOwner() {
        return (!getOwnedStores().isEmpty());
    }

    public LinkedList<Notification> getAllNotifications() {
        return new LinkedList<>(notifications.keySet());
    }

    public Market.StatsType visitorType() {
        int founded=getFoundedStores().size();
        int owned=getOwnedStores().size();
        int managed= getManagedStores().size();

        if(isSystemManager)
            return Market.StatsType.AdminVisitor;

        if (founded==0 && managed==0 && owned==0)
            return Market.StatsType.NonStaffVisitor;

        if(founded==0 && owned==0 && managed>0)
            return Market.StatsType.ManagerVisitor;

        if((founded>0 || owned>0) && managed==0)
            return Market.StatsType.OwnerVisitor;

        return null;
    }

    public Observer getObserver() {
        return this.observer;
    }

    public boolean isGuest() {
        return state.isGuest();
    }
}
