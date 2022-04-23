package main.Stores;



import main.NotificationBus;
import main.Shopping.ShoppingBasket;

import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
import main.utils.Pair;


import java.time.LocalDateTime;
import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
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
    private ConcurrentLinkedQueue<Pair<String, String>> messagesToStore;
    private ConcurrentHashMap<ShoppingBasket, LocalDateTime> purchaseHistory;
    private ConcurrentLinkedQueue<ShoppingBasket> buyingBaskets;


    public List<User> getOwnersOfStore() {
        LinkedList<User> storeOwners = new LinkedList<>();
        for (OwnerPermissions ow : owners) {
            storeOwners.add(ow.getAppointedToOwner());
        }
        return storeOwners;
    }

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
        messagesToStore = new ConcurrentLinkedQueue<>();
        purchaseHistory = new ConcurrentHashMap<>();
        buyingBaskets = new ConcurrentLinkedQueue<>();
		this.storeReviews = new LinkedList<>();
    }

    public boolean addProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if (productsByName.containsKey(productName))
            throw new IllegalArgumentException("There's already such product with this name in the store");

        Product product = new Product(productName, category, keyWords, description, quantity, price);
        productsByName.put(productName, product);
        return true;
    }

    public boolean updateProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        Product product = productsByName.get(productName);
        if (product == null)
            throw new IllegalArgumentException("No such product in the store!");
        String prevName = product.getName();
        if (!prevName.equals(productName)) { //name is changed
            if (productsByName.containsKey(productName)) //the name is already taken
                throw new IllegalArgumentException("There's already a product with that name!");
        }
        product.setProperties(productName, category, keyWords, description, quantity, price);

        productsByName.remove(prevName);
        productsByName.put(productName, product);
        return true;
    }

    public ConcurrentLinkedQueue<OwnerPermissions> getOwnersAppointments() {
        return owners;
    }

    public ConcurrentLinkedQueue<ManagerPermissions> getManagersAppointments() {
        return managers;
    }

    public void addOwnerToStore(OwnerPermissions newOwnerAppointment) {
        owners.add(newOwnerAppointment);
    }

    public void addManager(ManagerPermissions newManagerAppointment) {
        managers.add(newManagerAppointment);
    }

    public void removeManager(ManagerPermissions mp) {
        managers.remove(mp);
    }

    public void removeOwner(OwnerPermissions ow) {
        owners.remove(ow);
    }

    public ConcurrentHashMap<String, Product> getProductsByName() {
        return productsByName;
    }

    public synchronized void closeStore(NotificationBus bus) {
        if (!isActive)
            throw new IllegalArgumentException("The store is already closed!");
        isActive = false;
        sendMessageToStaffOfStore(String.format("The store %s is now inactive!", getName()), bus);
    }

    public Product getProduct(String name) {
        return productsByName.get(name);

    }

    private void sendMessageToStaffOfStore(String msg, NotificationBus bus) {
        for (User u : getOwnersOfStore())
            bus.addMessage(u, msg);
        for (User u : getManagersOfStore())
            bus.addMessage(u, msg);
    }

    public String getName() {
        return storeName;
    }


    public synchronized void reOpen(NotificationBus bus) {
        if (isActive)
            throw new IllegalArgumentException("The store is already opened!");
        isActive = true;
        sendMessageToStaffOfStore(String.format("The store %s is now active again!", getName()), bus);
    }

    public HashMap<User, String> getStoreStaff() {
        HashMap<User, String> staff = new HashMap<>();
        //founder
        staff.put(founder, "Founder of the store");

        //owners
        for (User owner : getOwnersOfStore())
            staff.put(owner, "Owner of the store");

        //managers
        for (ManagerPermissions managerPermission : managers)
            staff.put(managerPermission.getAppointedToManager(), managerPermission.permissionsToString());

        return staff;

    }

    public List<Pair<String,String>> getQuestions() {
        List<Pair<String,String>> msgList = new LinkedList<>();
        while (!messagesToStore.isEmpty())
            msgList.add(messagesToStore.remove());
        return msgList;
    }

    public boolean respondToBuyer(User toRespond, String msg, NotificationBus bus) {
        bus.addMessage(toRespond, msg);
        // here we can add any history of messages between user-store if necessary
        return true;
    }

    public ConcurrentHashMap<ShoppingBasket, LocalDateTime> getPurchaseHistory() {
        return purchaseHistory;
    }

    public void CancelStaffRoles() {
        //first removing founder
        founder.removeFounderRole(this);

        //then removing all owners
        for (OwnerPermissions owner : owners) {
            owner.getAppointedToOwner().removeOwnerRole(owner);
        }

        //finally, removing all managers
        for (ManagerPermissions manager : managers) {
            manager.getAppointedToManager().removeManagerRole(manager);
        }
    }

    public boolean removeProduct(String productName) {
        Product toRemove=productsByName.get(productName);
        if(toRemove==null)
            throw new IllegalArgumentException("No such product with this name");
        return productsByName.remove(productName)!=null;

    }

    public void addReview(StoreReview sReview) {
        this.storeReviews.add(sReview);
    }

    public void subtractProductQuantity(Product product, Integer quantity) throws Exception {
        if(product.getQuantity()<quantity)
        {
            throw new Exception("Not enough products in stock");
        }
        if(product.getQuantity()==quantity)
        {
            removeProduct(product.getName());
            return;
        }
        product.subtractQuantity(quantity);
    }
}
