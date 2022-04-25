package main.Stores;

import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
import main.utils.Pair;


import javax.swing.*;
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
    private ConcurrentHashMap<ShoppingBasket, LocalDateTime> purchaseHistory;
    private ConcurrentLinkedQueue<ShoppingBasket> buyingBaskets;

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
        purchaseHistory = new ConcurrentHashMap<>();
        buyingBaskets = new ConcurrentLinkedQueue<>();
        this.storeReviews = new LinkedList<>();
    }

    @Override
    public boolean addProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if (productsByName.containsKey(productName))
            throw new IllegalArgumentException("There's already such product with this name in the store");

        Product product = new Product(productName, category, keyWords, description, quantity, price);
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

    public synchronized void closeStore(NotificationBus bus) {
        if (!isActive)
            throw new IllegalArgumentException("The store is already closed!");
        isActive = false;
        sendMessageToStaffOfStore(String.format("The store %s is now inactive!", getName()), bus);
    }

    @Override
    public ConcurrentHashMap<String, Product> getProductsByName() {
        return productsByName;
    }

    @Override
    public Product getProduct(String name) {
        return productsByName.get(name);
    }

    private void sendMessageToStaffOfStore(String msg, NotificationBus bus) {
        bus.addMessage(founder, msg);
        for (User u : getOwnersOfStore())
            bus.addMessage(u, msg);
        for (User u : getManagersOfStore())
            bus.addMessage(u, msg);
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
    public synchronized void reOpen(NotificationBus bus) {
        if (isActive)
            throw new IllegalArgumentException("The store is already opened!");
        isActive = true;
        sendMessageToStaffOfStore(String.format("The store %s is now active again!", getName()), bus);
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
    public boolean respondToBuyer(User toRespond, String msg, NotificationBus bus) {
        bus.addMessage(toRespond, msg);
        // here we can add any history of messages between user-store if necessary
        return true;
    }

    @Override
    public ConcurrentHashMap<ShoppingBasket, LocalDateTime> getPurchaseHistory() {
        return purchaseHistory;
    }

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
    public void purchaseBasket(NotificationBus bus,ShoppingBasket bask) throws Exception {
        for (Map.Entry<Product,Integer> en : bask.getProductsAndQuantities().entrySet())
            purchaseProduct(en.getKey(), en.getValue());
        this.purchaseHistory.put(bask,LocalDateTime.now());
        notifyPurchase(bus);
    }

    private void notifyPurchase(NotificationBus bus) {
        for (User manager: getOwnersOfStore())
            bus.addMessage(manager, "Product/s were bought from your store!");
    }

    @Override
    public void addReview(StoreReview sReview) {
        for (StoreReview sr : this.storeReviews)
            if(sr.getUser().equals(sReview.getUser()))
                throw new IllegalArgumentException("User already wrote a review.");
        this.storeReviews.add(sReview);
    }

    /***
     * @param product to check
     * @param amount to buy
     * @return returns if its purchesable for the current amount.
     */
    @Override
    public boolean ValidateProduct(Product product, Integer amount) {
        return this.getIsActive() && product.getQuantity() >= amount;
    }

    private void purchaseProduct(Product product, Integer quantity) throws Exception {
        if (product.getQuantity() < quantity) {
            throw new Exception("Not enough products in stock");
        }
        if (product.getQuantity() == quantity) {
            removeProduct(product.getName());
            return;
        }
        product.subtractQuantity(quantity);
    }
}
