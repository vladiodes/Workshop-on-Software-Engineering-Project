package main.Stores;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Publisher.Notification;
import main.Publisher.PersonalNotification;
import main.Publisher.StoreNotification;
import main.Shopping.ShoppingBasket;
import main.Stores.SingleProductDiscounts.ConditionalDiscount;
import main.Stores.SingleProductDiscounts.DirectDiscount;
import main.Stores.SingleProductDiscounts.SecretDiscount;
import main.Stores.PurchasePolicy.AuctionPolicy;
import main.Stores.PurchasePolicy.BargainingPolicy;
import main.Stores.PurchasePolicy.normalPolicy;
import main.Stores.PurchasePolicy.rafflePolicy;
import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
import main.utils.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Store implements IStore {

    private ConcurrentHashMap<String, Product> productsByName;
    private ConcurrentLinkedQueue<OwnerPermissions> owners;
    private ConcurrentLinkedQueue<ManagerPermissions> managers;
    private User founder;
    private boolean isActive;
    private String storeName;
    private List<StoreReview> storeReviews;
    private ConcurrentHashMap<ShoppingBasket, LocalDateTime> purchaseHistoryByTime;
    private ConcurrentHashMap<ShoppingBasket, User> purchaseHistoryByUser;
    private ConcurrentLinkedQueue<ShoppingBasket> buyingBaskets;

    private ConcurrentLinkedQueue<PersonalNotification> storeQuestions;

    @Override
    public List<User> getOwnersOfStore() {
        LinkedList<User> storeOwners = new LinkedList<>();
        for (OwnerPermissions ow : owners) {
            storeOwners.add(ow.getAppointedToOwner());
        }
        return storeOwners;
    }

    @Override
    public List<User> getManagersOfStore() {
        LinkedList<User> storeManagers = new LinkedList<>();
        for (ManagerPermissions mp : managers) {
            storeManagers.add(mp.getAppointedToManager());
        }
        return storeManagers;
    }

    public Store(String storeName, User founder) {
        this.owners = new ConcurrentLinkedQueue<>();

        this.managers = new ConcurrentLinkedQueue<>();
        this.productsByName = new ConcurrentHashMap<>();
        isActive = true;
        this.storeName = storeName;
        this.founder = founder;
        purchaseHistoryByTime = new ConcurrentHashMap<>();
        buyingBaskets = new ConcurrentLinkedQueue<>();
        this.storeReviews = new LinkedList<>();
        this.purchaseHistoryByUser = new ConcurrentHashMap<>();
        this.storeQuestions=new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean addProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if (productsByName.containsKey(productName))
            throw new IllegalArgumentException("There's already such product with this name in the store");

        Product product = new Product(this,productName, category, keyWords, description, quantity, price);
        productsByName.put(productName, product);
        return true;
    }

    @Override
    public boolean updateProduct(String oldProductName, String newProductName, String category, List<String> keyWords, String description, int quantity, double price) {
        Product product = productsByName.get(oldProductName);
        if (product == null)
            throw new IllegalArgumentException("No such product in the store!");
        if (!oldProductName.equals(newProductName)) { //name is changed
            if (productsByName.containsKey(newProductName)) //the name is already taken
                throw new IllegalArgumentException("There's already a product with that name!");
        }
        product.setProperties(newProductName, category, keyWords, description, quantity, price);

        productsByName.remove(oldProductName);
        productsByName.put(newProductName, product);
        return true;
    }

    @Override
    public ConcurrentLinkedQueue<OwnerPermissions> getOwnersAppointments() {
        return owners;
    }

    @Override
    public ConcurrentLinkedQueue<ManagerPermissions> getManagersAppointments() {
        return managers;
    }

    @Override
    public void addOwnerToStore(OwnerPermissions newOwnerAppointment) {
        owners.add(newOwnerAppointment);
    }

    @Override
    public void addManager(ManagerPermissions newManagerAppointment) {
        managers.add(newManagerAppointment);
    }

    @Override
    public void removeManager(ManagerPermissions mp) {
        managers.remove(mp);
    }

    @Override
    public void removeOwner(OwnerPermissions ow) {
        owners.remove(ow);
    }

    public synchronized void closeStore() {
        if (!isActive)
            throw new IllegalArgumentException("The store is already closed!");
        isActive = false;
        sendMessageToStaffOfStore(new StoreNotification(storeName,"The store is now inactive"));
    }

    @Override
    public ConcurrentHashMap<String, Product> getProductsByName() {
        return productsByName;
    }

    @Override
    public Product getProduct(String name) {
        if(!productsByName.containsKey(name))
            throw new IllegalArgumentException("Request product doesn't exist");
        return productsByName.get(name);
    }

    @Override
    public void sendMessageToStaffOfStore(Notification notification) {
        founder.notifyObserver(notification);
        for (User u : getOwnersOfStore())
            u.notifyObserver(notification);
        for (User u : getManagersOfStore())
            u.notifyObserver(notification);
    }

    @Override
    public List<String> getStoreMessages() {
        LinkedList<String> lst = new LinkedList<>();
        for(PersonalNotification notification : storeQuestions){
            lst.add(notification.print());
        }
        return lst;
    }

    @Override
    public void addQuestionToStore(String userName, String message) {
        PersonalNotification n = new PersonalNotification(userName,message);
        storeQuestions.add(n);
        sendMessageToStaffOfStore(n);
    }

    @Override
    public String getName() {
        return storeName;
    }

    @Override
    public Boolean getIsActive() {
        return isActive;
    }

    @Override
    public synchronized void reOpen() {
        if (isActive)
            throw new IllegalArgumentException("The store is already opened!");
        isActive = true;
        sendMessageToStaffOfStore(new StoreNotification(storeName,"The store is now open again"));
    }

    @Override
    public HashMap<User, String> getStoreStaff() {
        HashMap<User, String> staff = new HashMap<>();
        //founder
        staff.put(founder, "Founder of the store");

        //owners
        for (User owner : getOwnersOfStore())
            staff.put(owner, "Owner of the store");

        //managers
        for (ManagerPermissions managerPermission : managers)
            staff.put(managerPermission.getAppointedToManager(), "Manager of the store, has permissions: " + managerPermission.permissionsToString());

        return staff;
    }

    @Override
    public boolean respondToBuyer(User toRespond, String msg) {
        toRespond.notifyObserver(new PersonalNotification(storeName,msg));
        // here we can add any history of messages between user-store if necessary
        return true;
    }

    @Override
    public ConcurrentHashMap<ShoppingBasket, LocalDateTime> getPurchaseHistoryByTime() {
        return purchaseHistoryByTime;
    }
    public ConcurrentHashMap<ShoppingBasket, User> getPurchaseHistoryByUser() {return this.purchaseHistoryByUser;}
    @Override
    public void CancelStaffRoles() {
        //first removing founder
        founder.removeFounderRole(this);
        this.founder = null;

        //then removing all owners
        for (OwnerPermissions owner : owners) {
            owner.getAppointedToOwner().removeOwnerRole(owner);
            this.owners.remove(owner);
        }

        //finally, removing all managers
        for (ManagerPermissions manager : managers) {
            manager.getAppointedToManager().removeManagerRole(manager);
            this.managers.remove(manager);
        }
    }

    @Override
    public boolean removeProduct(String productName) {
        Product toRemove = productsByName.get(productName);
        if (toRemove == null)
            throw new IllegalArgumentException("No such product with this name");
        return productsByName.remove(productName) != null;
    }

    @Override
    public void purchaseBasket(User user, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment, ShoppingBasket bask) {
        for (Map.Entry<Product,Integer> en : bask.getProductsAndQuantities().entrySet())
            en.getKey().Purchase(user, bask.getCostumePriceForProduct(en.getKey()), bask.getProductsAndQuantities().get(en.getKey()) ,supplying, supplyingInformation, paymentInformation, payment);
        this.purchaseHistoryByTime.put(bask,LocalDateTime.now());
        this.purchaseHistoryByUser.put(bask, user);
        notifyPurchase();
    }

    private void notifyPurchase() {
        for (User manager: getOwnersOfStore())
            manager.notifyObserver(new PersonalNotification(storeName,"Products were bought from your store!"));
    }

    @Override
    public void addReview(StoreReview sReview) {
        for (StoreReview sr : this.storeReviews)
            if(sr.getUser().equals(sReview.getUser()))
                throw new IllegalArgumentException("User already wrote a review.");
        this.storeReviews.add(sReview);
    }

    @Override
    public void notifyBargainingStaff(Bid newbid) {
        for (User staff: getStoreStaff().keySet())
            if(staff.ShouldBeNotfiedForBargaining(this))
                staff.notifyObserver(new PersonalNotification(
                        storeName,
                        String.format("A new bargain offer on product %s from %s.", newbid.getProduct().getName(), newbid.getUser().getUserName())));
    }


    @Override
    public void addDirectDiscount(String productName, LocalDate until, Double percent) {
        Product product = getProduct(productName);
        product.setDiscount(new DirectDiscount(percent, until));
    }

    @Override
    public void addSecretDiscount(String productName, LocalDate until, Double percent, String secretCode) {
        Product product = getProduct(productName);
        product.setDiscount(new SecretDiscount(percent, until, secretCode));
    }

    @Override
    public void addConditionalDiscount(String productName, LocalDate until, Restriction restrictions, Double percent) {
        Product product = getProduct(productName);
        product.setDiscount(new ConditionalDiscount(restrictions, percent, until));
    }

    @Override
    public void addRafflePolicy(String productName, Double price) {
        Product product = getProduct(productName);
        product.setPolicy(new rafflePolicy(this, price));
    }

    @Override
    public void addAuctionPolicy(String productName, Double price, LocalDate until) {
        Product product = getProduct(productName);
        product.setPolicy(new AuctionPolicy(until, price,this, productName));
    }

    @Override
    public void addNormalPolicy(String productName, Double price) {
        Product product = getProduct(productName);
        product.setPolicy(new normalPolicy(price, this));
    }

    @Override
    public void addBargainPolicy(String productName,Double originalPrice) {
        Product product = getProduct(productName);
        product.setPolicy(new BargainingPolicy(this, originalPrice, product));
    }

    @Override
    public boolean bidOnProduct(String productName, Bid bid) {
        Product product = getProduct(productName);
        if (product.bid(bid)){
            notifyBargainingStaff(bid);
            return true;
        }
        return false;
    }

}
