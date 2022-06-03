package main.Stores;

import main.DTO.ShoppingBasketDTO;
import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Publisher.Notification;
import main.Publisher.PersonalNotification;
import main.Publisher.StoreNotification;
import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalAndCondition;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalOrCondition;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalXorCondition;
import main.Stores.PurchasePolicy.Conditions.Condition;
import main.Stores.PurchasePolicy.Conditions.SimpleConditions.BasketValueCondition;
import main.Stores.PurchasePolicy.Conditions.SimpleConditions.CategoryAmountCondition;
import main.Stores.PurchasePolicy.Conditions.SimpleConditions.ProductAmountCondition;
import main.Stores.PurchasePolicy.Discounts.*;
import main.Stores.PurchasePolicy.Discounts.CompositeDiscounts.MaximumCompositeDiscount;
import main.Stores.PurchasePolicy.Discounts.CompositeDiscounts.PlusCompositeDiscount;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.ConditionalDiscount;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.SecretDiscount;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.SimpleDiscount;
import main.Stores.PurchasePolicy.ProductPolicy.AuctionPolicy;
import main.Stores.PurchasePolicy.ProductPolicy.BargainingPolicy;
import main.Stores.PurchasePolicy.ProductPolicy.normalPolicy;
import main.Stores.PurchasePolicy.ProductPolicy.rafflePolicy;
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
import javax.persistence.*;

@Entity
public class Store implements IStore {

    @Id
    private ConcurrentHashMap<String, Product> productsByName;
    private ConcurrentLinkedQueue<OwnerPermissions> owners;
    private ConcurrentLinkedQueue<ManagerPermissions> managers;
    @OneToOne
    private User founder;
    private boolean isActive;
    private String storeName;
    private List<StoreReview> storeReviews;
    private ConcurrentHashMap<ShoppingBasketDTO, LocalDateTime> purchaseHistoryByTime;
    private ConcurrentHashMap<Integer, Discount> DiscountsInStore;
    private ConcurrentHashMap<Integer, Condition> ConditionsInStore;
    @OneToOne
    private Discount StoreDiscount;
    @OneToOne
    private Condition StorePurchaseCondition;

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
        DiscountsInStore = new ConcurrentHashMap<>();
        ConditionsInStore = new ConcurrentHashMap<>();
        StoreDiscount = null;
        StorePurchaseCondition = null;
        this.owners = new ConcurrentLinkedQueue<>();
        this.managers = new ConcurrentLinkedQueue<>();
        this.productsByName = new ConcurrentHashMap<>();
        isActive = true;
        this.storeName = storeName;
        this.founder = founder;
        purchaseHistoryByTime = new ConcurrentHashMap<>();
        this.storeReviews = new LinkedList<>();
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

    private synchronized <K, V> int getID(ConcurrentHashMap<K, V> map){
        return map.size();
    }

    private Condition getConditionbyID(int id){
        if (!this.ConditionsInStore.containsKey(id))
            throw new IllegalArgumentException("Requested condition ID doesn't exist");
        return ConditionsInStore.get(id);
    }

    private Discount getDiscountByID(int id){
        if (!this.DiscountsInStore.containsKey(id))
            throw new IllegalArgumentException("Requested condition ID doesn't exist");
        return DiscountsInStore.get(id);
    }

    @Override
    public int CreateSimpleDiscount(LocalDate until, Double percent) {
        SimpleDiscount disc = new SimpleDiscount(until, percent);
        int id = getID(this.DiscountsInStore);
        this.DiscountsInStore.put(id, disc);
        return id;
    }

    @Override
    public int CreateSecretDiscount(LocalDate until, Double percent, String secretCode) {
        SecretDiscount disc = new SecretDiscount(until, percent, secretCode);
        int id = getID(this.DiscountsInStore);
        this.DiscountsInStore.put(id, disc);
        return id;
    }

    @Override
    public int CreateConditionalDiscount(LocalDate until, Double percent, int condID) {
        ConditionalDiscount disc = new ConditionalDiscount(until, percent, getConditionbyID(condID));
        int id = getID(this.DiscountsInStore);
        this.DiscountsInStore.put(id, disc);
        return id;
    }

    @Override
    public int CreateMaximumCompositeDiscount(LocalDate until, List<Integer> discounts) {
        MaximumCompositeDiscount disc = new MaximumCompositeDiscount(until);
        int id = getID(this.DiscountsInStore);
        this.DiscountsInStore.put(id, disc);
        for(Integer discid : discounts)
            disc.addDiscount(getDiscountByID(discid));
        return id;
    }

    @Override
    public int CreatePlusCompositeDiscount(LocalDate until, List<Integer> discounts) {
        PlusCompositeDiscount disc = new PlusCompositeDiscount(until);
        int id = getID(this.DiscountsInStore);
        this.DiscountsInStore.put(id, disc);
        for(Integer discid : discounts)
            disc.addDiscount(getDiscountByID(discid));
        return id;
    }

    @Override
    public void SetDiscountToProduct(int discountID, String productName) {
        if (discountID == -1)
            this.getProduct(productName).setDiscount(null);
        else this.getProduct(productName).setDiscount(getDiscountByID(discountID));
    }

    @Override
    public void SetDiscountToStore(int discountID) {
        if (discountID == -1)
            this.StoreDiscount = null;
        else this.StoreDiscount = this.getDiscountByID(discountID);
    }

    @Override
    public int CreateBasketValueCondition(double requiredValue) {
        Condition cond = new BasketValueCondition(requiredValue);
        int out = getID(ConditionsInStore);
        ConditionsInStore.put(out, cond);
        return out;
    }

    @Override
    public int CreateCategoryAmountCondition(String category, int amount) {
        Condition cond = new CategoryAmountCondition(category, amount);
        int out = getID(ConditionsInStore);
        ConditionsInStore.put(out, cond);
        return out;
    }

    @Override
    public int CreateProductAmountCondition(String productName, int amount) {
        Condition cond = new ProductAmountCondition(amount, getProduct(productName));
        int out = getID(ConditionsInStore);
        ConditionsInStore.put(out, cond);
        return out;
    }

    @Override
    public int CreateLogicalAndCondition(List<Integer> conditionIds) {
        Condition cond = new LogicalAndCondition();
        int out = getID(ConditionsInStore);
        for(Integer condid : conditionIds)
            cond.addCondition(getConditionbyID(condid));
        ConditionsInStore.put(out, cond);
        return out;
    }

    @Override
    public int CreateLogicalOrCondition(List<Integer> conditionIds) {
        Condition cond = new LogicalOrCondition();
        int out = getID(ConditionsInStore);
        for(Integer condid : conditionIds)
            cond.addCondition(getConditionbyID(condid));
        ConditionsInStore.put(out, cond);
        return out;
    }

    @Override
    public int CreateLogicalXorCondition(int id1, int id2) {
        Condition cond = new LogicalXorCondition();
        int out = getID(ConditionsInStore);
        cond.addCondition(getConditionbyID(id1));
        cond.addCondition(getConditionbyID(id2));
        ConditionsInStore.put(out, cond);
        return out;
    }

    @Override
    public void SetConditionToDiscount(int discountId, int ConditionID) {
        getDiscountByID(discountId).setCondition(getConditionbyID(ConditionID));
    }

    @Override
    public void SetConditionToStore(int ConditionID) {
        if(ConditionID == -1)
            this.StorePurchaseCondition = null;
        else this.StorePurchaseCondition = getConditionbyID(ConditionID);
    }

    @Override
    public double getPriceForProduct(Product product, User user) {
        if(this.StoreDiscount != null)
            return StoreDiscount.getPriceFor(product.getCurrentPrice(user), user.getCart().getBasket(this.getName()));
        else return product.getCurrentPrice(user);
    }

    @Override
    public boolean ValidateBasket(User user, ShoppingBasket shoppingBasket) {
        boolean res = this.getIsActive();
        if(StorePurchaseCondition != null)
            res &= StorePurchaseCondition.pass(shoppingBasket);
        for (Map.Entry<Product, Integer> ent: shoppingBasket.getProductsAndQuantities().entrySet() ) {
            res &=  ent.getKey().isPurchasableForAmount(ent.getValue());
            if(shoppingBasket.getCostumePriceForProduct(ent.getKey()) != null)
                res &= ent.getKey().isPurchasableForPrice(shoppingBasket.getCostumePriceForProduct(ent.getKey()), ent.getValue(), user);
        }
        return res;
    }

    @Override
    public boolean isProductAddable(String productName) {
        return productsByName.get(productName)!=null && productsByName.get(productName).isAddableToBasket();
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
    public ConcurrentHashMap<ShoppingBasketDTO, LocalDateTime> getPurchaseHistoryByTime() {
        return purchaseHistoryByTime;
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
    public void purchaseBasket(User user, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment, ShoppingBasket bask) {
        for (Map.Entry<Product,Integer> en : bask.getProductsAndQuantities().entrySet())
            en.getKey().Purchase(user, bask.getCostumePriceForProduct(en.getKey()), bask.getProductsAndQuantities().get(en.getKey()) ,supplying, supplyingInformation, paymentInformation, payment);
        ShoppingBasketDTO basketDTO = new ShoppingBasketDTO(bask,user);
        this.purchaseHistoryByTime.put(basketDTO,LocalDateTime.now());
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
