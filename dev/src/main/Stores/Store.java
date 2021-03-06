package main.Stores;

import main.DTO.ShoppingBasketDTO;
import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Persistence.DAO;
import main.Publisher.Notification;
import main.Publisher.PersonalNotification;
import main.Publisher.StoreNotification;
import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalAndPurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalOrPurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalXorPurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.PurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.SimpleConditions.BasketValuePurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.SimpleConditions.CategoryAmountPurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.SimpleConditions.ProductAmountPurchaseCondition;
import main.Stores.PurchasePolicy.Discounts.*;
import main.Stores.PurchasePolicy.Discounts.CompositeDiscounts.MaximumCompositeDiscount;
import main.Stores.PurchasePolicy.Discounts.CompositeDiscounts.PlusCompositeDiscount;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.ConditionalDiscount;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.SecretDiscount;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.SimpleDiscount;
import main.Stores.PurchasePolicy.ProductPolicy.*;
import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
import main.utils.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.*;

@Entity
public class Store {

    @Id
    @GeneratedValue
    private int store_id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "store_products",
            joinColumns = {@JoinColumn(name="store_id",referencedColumnName = "store_id")},
            inverseJoinColumns = {@JoinColumn(name="product_id",referencedColumnName = "id")})
    @MapKey(name="productName")
    private Map<String, Product> productsByName;
    @OneToMany(cascade = CascadeType.ALL)
    private Collection<OwnerAppointmentRequest> ownerAppointmentRequests;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<OwnerPermissions> owners;
    @OneToMany(cascade = CascadeType.ALL)
    private Collection<ManagerPermissions> managers;

    @OneToOne
    private User founder;
    private boolean isActive;

    private String storeName;
    @OneToMany(cascade = CascadeType.ALL)
    private List<StoreReview> storeReviews;
    @ElementCollection
    private Map<ShoppingBasketDTO, LocalDateTime> purchaseHistoryByTime;
    @OneToMany(cascade = CascadeType.ALL)
    @MapKey(name="id_in_store")
    private Map<Integer, Discount> DiscountsInStore;
    @OneToMany(cascade = CascadeType.ALL)
    @MapKey(name="id_in_store")
    private Map<Integer, PurchaseCondition> ConditionsInStore;

    @OneToOne(cascade = CascadeType.ALL)
    private Discount StoreDiscount;

    @OneToOne(cascade = CascadeType.ALL)
    private PurchaseCondition storePurchasePurchaseCondition;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<PersonalNotification> storeQuestions;

    public Store() {

    }

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
        DiscountsInStore = Collections.synchronizedMap(new HashMap<>());
        ConditionsInStore = Collections.synchronizedMap(new HashMap<>());
        StoreDiscount = null;
        storePurchasePurchaseCondition = null;
        this.owners = Collections.synchronizedList(new LinkedList<>());
        this.managers = Collections.synchronizedList(new LinkedList<>());
        this.productsByName = Collections.synchronizedMap(new HashMap<>());
        isActive = true;
        this.storeName = storeName;
        this.founder = founder;
        purchaseHistoryByTime = Collections.synchronizedMap(new HashMap<>());
        this.storeReviews = new LinkedList<>();
        this.storeQuestions=Collections.synchronizedList(new LinkedList<>());
        this.ownerAppointmentRequests = Collections.synchronizedList(new LinkedList<>());
    }

    public boolean addProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if (productsByName.containsKey(productName))
            throw new IllegalArgumentException("There's already such product with this name in the store");

        Product product = new Product(this,productName, category, keyWords, description, quantity, price);
        DAO.getInstance().persist(product);
        productsByName.put(productName, product);
        DAO.getInstance().merge(this);
        return true;
    }

    public double getAverageReview(){
        double output = 0;
        for(StoreReview rs : this.storeReviews)
            output += rs.getPoints();
        if(this.storeReviews.size() == 0)
            return 0;
        else
            return output / this.storeReviews.size();
    }

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
        DAO.getInstance().merge(product);
        return true;
    }

    public Collection<OwnerPermissions> getOwnersAppointments() {
        return owners;
    }

    public Collection<ManagerPermissions> getManagersAppointments() {
        return managers;
    }

    public void addOwnerToStore(OwnerPermissions newOwnerAppointment) {
        owners.add(newOwnerAppointment);
        DAO.getInstance().merge(this);
    }

    public void addManager(ManagerPermissions newManagerAppointment) {
        managers.add(newManagerAppointment);
    }

    public void removeManager(ManagerPermissions mp) {
        managers.remove(mp);
        DAO.getInstance().merge(this);
        notifyProductsStaffChange();
    }

    public void removeOwner(OwnerPermissions ow) {
        owners.remove(ow);
        DAO.getInstance().merge(this);

        Collection<OwnerAppointmentRequest> approvedRequests = getApprovedRequests();
        for(OwnerAppointmentRequest req : approvedRequests) {
            executeNewOwnerRequest(req);
        }
        notifyProductsStaffChange();
    }

    public synchronized void closeStore() {
        if (!isActive)
            throw new IllegalArgumentException("The store is already closed!");
        isActive = false;
        DAO.getInstance().merge(this);
        Notification n =new StoreNotification(storeName,"The store is now inactive");
        DAO.getInstance().persist(n);
        sendMessageToStaffOfStore(n);
    }

    public Map<String, Product> getProductsByName() {
        return productsByName;
    }

    public Product getProduct(String name) {
        if(!productsByName.containsKey(name))
            throw new IllegalArgumentException("Request product doesn't exist");
        return productsByName.get(name);
    }

    public void sendMessageToStaffOfStore(Notification notification) {
        founder.notifyObserver(notification);
        for (User u : getOwnersOfStore())
            u.notifyObserver(notification);
        for (User u : getManagersOfStore())
            u.notifyObserver(notification);
    }

    public List<String> getStoreMessages() {
        LinkedList<String> lst = new LinkedList<>();
        for(PersonalNotification notification : storeQuestions){
            lst.add(notification.print());
        }
        return lst;
    }

    public void addQuestionToStore(String userName, String message) {
        PersonalNotification n = new PersonalNotification(userName,message);
        DAO.getInstance().persist(n);
        storeQuestions.add(n);
        sendMessageToStaffOfStore(n);
    }

    private synchronized <K, V> int getID(Map<K, V> map){
        return map.size();
    }

    private PurchaseCondition getConditionbyID(int id){
        if (!this.ConditionsInStore.containsKey(id))
            throw new IllegalArgumentException("Requested condition ID doesn't exist");
        return ConditionsInStore.get(id);
    }

    private Discount getDiscountByID(int id){
        if (!this.DiscountsInStore.containsKey(id))
            throw new IllegalArgumentException("Requested condition ID doesn't exist");
        return DiscountsInStore.get(id);
    }

    public int CreateSimpleDiscount(LocalDate until, Double percent) {
        SimpleDiscount disc = new SimpleDiscount(until, percent);
        DAO.getInstance().persist(disc);
        int id = getID(this.DiscountsInStore);
        disc.setId_in_store(id);
        this.DiscountsInStore.put(id, disc);
        DAO.getInstance().merge(disc);
        DAO.getInstance().merge(this);
        return id;
    }

    public int CreateSecretDiscount(LocalDate until, Double percent, String secretCode) {
        SecretDiscount disc = new SecretDiscount(until, percent, secretCode);
        DAO.getInstance().persist(disc);
        int id = getID(this.DiscountsInStore);
        disc.setId_in_store(id);
        this.DiscountsInStore.put(id, disc);
        DAO.getInstance().merge(disc);
        DAO.getInstance().merge(this);
        return id;
    }

    public int CreateConditionalDiscount(LocalDate until, Double percent, int condID) {
        ConditionalDiscount disc = new ConditionalDiscount(until, percent, getConditionbyID(condID));
        DAO.getInstance().persist(disc);
        int id = getID(this.DiscountsInStore);
        disc.setId_in_store(id);
        this.DiscountsInStore.put(id, disc);
        DAO.getInstance().merge(disc);
        DAO.getInstance().merge(this);
        return id;
    }

    public int CreateMaximumCompositeDiscount(LocalDate until, List<Integer> discounts) {
        MaximumCompositeDiscount disc = new MaximumCompositeDiscount(until);
        DAO.getInstance().persist(disc);
        int id = getID(this.DiscountsInStore);
        this.DiscountsInStore.put(id, disc);
        disc.setId_in_store(id);
        for(Integer discid : discounts)
            disc.addDiscount(getDiscountByID(discid));
        DAO.getInstance().merge(disc);
        DAO.getInstance().merge(this);
        return id;
    }

    public int CreatePlusCompositeDiscount(LocalDate until, List<Integer> discounts) {
        PlusCompositeDiscount disc = new PlusCompositeDiscount(until);
        DAO.getInstance().persist(disc);
        int id = getID(this.DiscountsInStore);
        this.DiscountsInStore.put(id, disc);
        disc.setId_in_store(id);
        for(Integer discid : discounts)
            disc.addDiscount(getDiscountByID(discid));
        DAO.getInstance().merge(disc);
        DAO.getInstance().merge(this);
        return id;
    }

    public void SetDiscountToProduct(int discountID, String productName) {
        if (discountID == -1)
            this.getProduct(productName).setDiscount(null);
        else this.getProduct(productName).setDiscount(getDiscountByID(discountID));
        DAO.getInstance().merge(getProduct(productName));
    }

    public void SetDiscountToStore(int discountID) {
        if (discountID == -1)
            this.StoreDiscount = null;
        else this.StoreDiscount = this.getDiscountByID(discountID);
        DAO.getInstance().merge(this);
    }

    public int CreateBasketValueCondition(double requiredValue) {
        PurchaseCondition cond = new BasketValuePurchaseCondition(requiredValue);
        int out = getID(ConditionsInStore);
        cond.setId_in_store(out);
        ConditionsInStore.put(out, cond);
        DAO.getInstance().persist(cond);
        DAO.getInstance().merge(this);
        return out;
    }

    public int CreateCategoryAmountCondition(String category, int amount) {
        PurchaseCondition cond = new CategoryAmountPurchaseCondition(category, amount);
        int out = getID(ConditionsInStore);
        cond.setId_in_store(out);
        ConditionsInStore.put(out, cond);
        DAO.getInstance().persist(cond);
        DAO.getInstance().merge(this);
        return out;
    }

    public int CreateProductAmountCondition(String productName, int amount) {
        PurchaseCondition cond = new ProductAmountPurchaseCondition(amount, getProduct(productName));
        int out = getID(ConditionsInStore);
        cond.setId_in_store(out);
        ConditionsInStore.put(out, cond);
        DAO.getInstance().persist(cond);
        DAO.getInstance().merge(this);
        return out;
    }

    public int CreateLogicalAndCondition(List<Integer> conditionIds) {
        PurchaseCondition cond = new LogicalAndPurchaseCondition();
        int out = getID(ConditionsInStore);
        cond.setId_in_store(out);
        DAO.getInstance().persist(cond);
        for(Integer condid : conditionIds)
            cond.addCondition(getConditionbyID(condid));

        ConditionsInStore.put(out, cond);
        DAO.getInstance().merge(cond);
        DAO.getInstance().merge(this);
        return out;
    }

    public int CreateLogicalOrCondition(List<Integer> conditionIds) {
        PurchaseCondition cond = new LogicalOrPurchaseCondition();
        DAO.getInstance().persist(cond);
        int out = getID(ConditionsInStore);
        cond.setId_in_store(out);
        for(Integer condid : conditionIds)
            cond.addCondition(getConditionbyID(condid));
        ConditionsInStore.put(out, cond);
        DAO.getInstance().merge(cond);
        DAO.getInstance().merge(this);
        return out;
    }

    public int CreateLogicalXorCondition(int id1, int id2) {
        PurchaseCondition cond = new LogicalXorPurchaseCondition();
        DAO.getInstance().persist(cond);
        int out = getID(ConditionsInStore);
        cond.setId_in_store(out);
        cond.addCondition(getConditionbyID(id1));
        cond.addCondition(getConditionbyID(id2));
        ConditionsInStore.put(out, cond);
        DAO.getInstance().merge(cond);
        DAO.getInstance().merge(this);
        return out;
    }

    public void SetConditionToDiscount(int discountId, int ConditionID) {
        getDiscountByID(discountId).setCondition(getConditionbyID(ConditionID));
        DAO.getInstance().merge(this);
    }

    public void SetConditionToStore(int ConditionID) {
        if(ConditionID == -1)
            this.storePurchasePurchaseCondition = null;
        else this.storePurchasePurchaseCondition = getConditionbyID(ConditionID);
        DAO.getInstance().merge(this);
    }

    public double getPriceForProduct(Product product, User user) {
        if(this.StoreDiscount != null)
            return StoreDiscount.getPriceFor(product.getCurrentPrice(user), user.getCart().getBasket(this.getName()));
        else return product.getCurrentPrice(user);
    }

    //Returns true if valid and throws exceptions if not. Never returns false.
    public boolean ValidateBasket(User user, ShoppingBasket shoppingBasket){
        boolean res = this.getIsActive();
        if(!res)
            throw new IllegalArgumentException("Store " + this.getName()+ " is inactive");
        if(storePurchasePurchaseCondition != null)
            res &= storePurchasePurchaseCondition.pass(shoppingBasket);
        if(!res)
            throw new IllegalArgumentException("Shopping basket doesnt pass the conditions of store "+ this.getName());
        for (Map.Entry<Product, Integer> ent: shoppingBasket.getProductsAndQuantities().entrySet() ) {
            res &=  ent.getKey().isPurchasableForAmount(ent.getValue());
            if(!res)
            {
                throw new IllegalArgumentException("Product " + ent.getKey().getName() + " is not purchasable for amount " + ent.getValue());
            }
            if(shoppingBasket.getCostumePriceForProduct(ent.getKey()) != null)
            {
                res &= ent.getKey().isPurchasableForPrice(shoppingBasket.getCostumePriceForProduct(ent.getKey()), ent.getValue(), user);
                if(!res)
                {
                    throw new IllegalArgumentException("Product " + ent.getKey().getName() + " is not purchasable for that price");
                }
            }
        }
        //Only returns true because no exceptions were thrown until here
        return res;
    }

    public boolean isProductAddable(String productName) {
        return productsByName.get(productName)!=null && productsByName.get(productName).isAddableToBasket();
    }

    public String getName() {
        return storeName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public synchronized void reOpen() {
        if (isActive)
            throw new IllegalArgumentException("The store is already opened!");
        isActive = true;
        DAO.getInstance().merge(this);
        Notification n =new StoreNotification(storeName,"The store is now open again");
        DAO.getInstance().persist(n);
        sendMessageToStaffOfStore(n);
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
            staff.put(managerPermission.getAppointedToManager(), "Manager of the store, has permissions: " + managerPermission.permissionsToString());

        return staff;
    }

    public boolean respondToBuyer(User toRespond, String msg) {
        Notification n =new PersonalNotification(storeName,msg);
        DAO.getInstance().persist(n);
        toRespond.notifyObserver(n);
        // here we can add any history of messages between user-store if necessary
        return true;
    }

    public Map<ShoppingBasketDTO, LocalDateTime> getPurchaseHistoryByTime() {
        return purchaseHistoryByTime;
    }

    public void CancelStaffRoles() {
        //first removing founder
        founder.removeFounderRole(this);
        this.founder = null;

        //then removing all owners
        for (OwnerPermissions owner : owners)
            owner.getAppointedToOwner().removeOwnerRole(owner);
        owners = Collections.synchronizedList(new LinkedList<>());


        //finally, removing all managers
        for (ManagerPermissions manager : managers)
            manager.getAppointedToManager().removeManagerRole(manager);
        managers = Collections.synchronizedList(new LinkedList<>());
    }

    public boolean removeProduct(String productName) {
        Product toRemove = productsByName.get(productName);
        if (toRemove == null)
            throw new IllegalArgumentException("No such product with this name");
        boolean output= productsByName.remove(productName) != null;
        DAO.getInstance().remove(toRemove);
        DAO.getInstance().merge(this);
        return output;
    }

    public void purchaseBasket(User user, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment, ShoppingBasket bask) {
        for (Map.Entry<Product,Integer> en : bask.getProductsAndQuantities().entrySet())
            en.getKey().Purchase(user, bask.getCostumePriceForProduct(en.getKey()), bask.getProductsAndQuantities().get(en.getKey()) ,supplying, supplyingInformation, paymentInformation, payment);
        ShoppingBasketDTO basketDTO = new ShoppingBasketDTO(bask,user,true);
        DAO.getInstance().persist(basketDTO);
        this.purchaseHistoryByTime.put(basketDTO,LocalDateTime.now());
        DAO.getInstance().merge(this);
        notifyPurchase();
    }

    private void notifyPurchase() { // notifies owners and founder.
        String notificationString = "Products were bought from your store!";
        for (User manager: getOwnersOfStore()){
            Notification n = new PersonalNotification(storeName,notificationString);
            DAO.getInstance().persist(n);
            manager.notifyObserver(n);
        }
        Notification n = new PersonalNotification(storeName,notificationString);
        DAO.getInstance().persist(n);
        founder.notifyObserver(n);
    }

    public void addReview(StoreReview sReview) {
        for (StoreReview sr : this.storeReviews)
            if(sr.getUser().equals(sReview.getUser()))
                throw new IllegalArgumentException("User already wrote a review.");
        this.storeReviews.add(sReview);
    }

    public void notifyBargainingStaff(Bid newbid) {
        for (User staff: getStoreStaff().keySet())
            if(staff.ShouldBeNotfiedForBargaining(this)){
                Notification n = new PersonalNotification(
                        storeName,
                        String.format("A new bargain offer on product %s from %s.", newbid.getProduct().getName(), newbid.getUser().getUserName()));
                DAO.getInstance().persist(n);
                staff.notifyObserver();
            }
    }

    public void notifyOwnersOnNewAppointmentRequest(OwnerAppointmentRequest req) {
        Notification n = new PersonalNotification(
                storeName,
                String.format("%s has requested to appoint %s to store owner", req.getRequestedBy().getUserName(), req.getUserToAppoint().getUserName())
        );
        DAO.getInstance().persist(n);
        for(User owner : getOwnersOfStore()) {
            if(owner != req.getRequestedBy())
                owner.notifyObserver(n);
        }
        if(this.founder != req.getRequestedBy())
            this.founder.notifyObserver(n);

    }

    public void addNormalPolicy(String productName, Double price) {
        Product product = getProduct(productName);
        Policy p =new normalPolicy(price, this);
        DAO.getInstance().persist(p);
        product.setPolicy(p);
        DAO.getInstance().merge(p);
    }

    public void addBargainPolicy(String productName,Double originalPrice) {
        Product product = getProduct(productName);
        Policy p =new BargainingPolicy(this, originalPrice, product);
        DAO.getInstance().persist(p);
        product.setPolicy(p);
        DAO.getInstance().merge(product);
    }

    public boolean bidOnProduct(String productName, Bid bid) {
        Product product = getProduct(productName);
        if (product.bid(bid)){
            notifyBargainingStaff(bid);
            return true;
        }
        return false;
    }


    public User getFounder() {
        return founder;
    }
    private void notifyAboutSuccessfullOwnerAppointment(OwnerAppointmentRequest request) {
        Notification ownerNotification = new PersonalNotification(
                this.storeName,
                String.format("%s owner appointment request has been approved by all owners",request.getUserToAppoint().getUserName())
        );
        DAO.getInstance().persist(ownerNotification);
        Notification newOwnerNotification = new PersonalNotification(
                this.storeName,
                "your owner appointment request has been approved"
        );
        DAO.getInstance().persist(newOwnerNotification);
        request.getRequestedBy().notifyObserver(ownerNotification);
        request.getUserToAppoint().notifyObserver(newOwnerNotification);
    }

    private void executeNewOwnerRequest(OwnerAppointmentRequest request) {
        User toAppoint = request.getUserToAppoint();
        User requestedBy = request.getRequestedBy();
        OwnerPermissions newOwnerAppointment = new OwnerPermissions(toAppoint, requestedBy, this);
        DAO.getInstance().persist(newOwnerAppointment);
        toAppoint.addOwnedStore(newOwnerAppointment);

        this.addOwnerToStore(newOwnerAppointment);
        this.notifyAboutSuccessfullOwnerAppointment(request);
        this.ownerAppointmentRequests.remove(request);
        DAO.getInstance().merge(this);
        DAO.getInstance().remove(request);
    }
    public boolean addOwnerRequest(OwnerAppointmentRequest request) {
        this.ownerAppointmentRequests.add(request);
        DAO.getInstance().merge(this);
        if(verifyRequest(request)) {
            // if the store has only 1 owner
            executeNewOwnerRequest(request);
        }
        else {
            // notify all owners and founder about new request they need to decide on
            notifyOwnersOnNewAppointmentRequest(request);
        }
        return true;
    }
    private boolean verifyRequest(OwnerAppointmentRequest request) {
        Collection<User> approves = request.getApprovedBy();
        if(!approves.contains(this.founder)) return false;

        for(User owner : getOwnersOfStore()) {
            if (!approves.contains(owner)) {
                return  false;
            }
        }
        return true;
    }
    private Collection<OwnerAppointmentRequest> getApprovedRequests() {
        Collection<OwnerAppointmentRequest> res = Collections.synchronizedList(new LinkedList<>());
        for(OwnerAppointmentRequest req : this.ownerAppointmentRequests) {
            if(verifyRequest(req)){
                res.add(req);
            }
        }
        return res;
    }

    private OwnerAppointmentRequest getOwnerAppointmentRequest(User userToAppoint) {
        for(OwnerAppointmentRequest req : ownerAppointmentRequests){
            if(req.getUserToAppoint() == userToAppoint) {
                return req;
            }
        }
        throw new IllegalArgumentException("User has no pending appointment request waiting");
    }

    private boolean verifyOwnerOrFounder(User u) {
        if (founder == u) return true;
        for(User owner : getOwnersOfStore()) {
            if(u == owner) return true;
        }
        return false;
    }

    public void approveOwnerRequest(User approver, User userToApprove) {
        OwnerAppointmentRequest request = getOwnerAppointmentRequest(userToApprove);
        boolean canVote = verifyOwnerOrFounder(approver);
        if(canVote){
            boolean voteRes = request.addVote(approver);
            if(!voteRes) {
                throw new IllegalArgumentException(String.format("%s already voted for this request", approver.getUserName()));
            }
            boolean shouldAppoint = verifyRequest(request);
            if(shouldAppoint) {
                executeNewOwnerRequest(request);
            }
        }
        else{
            throw new IllegalArgumentException("the approver is not owner/founder of the store");
        }
    }

    public void declineOwnerRequest(User refuser, User userToDecline) {
        OwnerAppointmentRequest request = getOwnerAppointmentRequest(userToDecline);
        boolean canVote = verifyOwnerOrFounder(refuser);
        if(canVote){

            Notification n = new PersonalNotification(
                    storeName,
                    String.format("%s owner appointment request was decline by %s", userToDecline.getUserName(), refuser.getUserName())
            );
            DAO.getInstance().persist(n);
            request.getRequestedBy().notifyObserver(n);
            userToDecline.notifyObserver(n);
            ownerAppointmentRequests.remove(request);
            DAO.getInstance().remove(request);
            DAO.getInstance().merge(this);
        }
        else{
            throw new IllegalArgumentException("the refuser is not owner/founder of the store");
        }
    }

    private List<OwnerAppointmentRequest> getNotVotedByUserRequests(User u) {
        List<OwnerAppointmentRequest> res = new LinkedList<>();
        for(OwnerAppointmentRequest req : ownerAppointmentRequests) {
            if(!req.didVote(u)) {
                res.add(req);
            }
        }
        return res;
    }

    public List<OwnerAppointmentRequest> getNotVotedOwnerAppointmentRequests(User u) {
        boolean canWatchRequests = verifyOwnerOrFounder(u);
        if(!canWatchRequests) {
            throw new IllegalArgumentException("user is not owner/founder of the store and cannot see the requests");
        }
        return getNotVotedByUserRequests(u);
    }

    public Collection<OwnerAppointmentRequest> getAllOwnerRequests() {
        return ownerAppointmentRequests;
    }

    public boolean containsRequestFor(User user_to_appoint) {
        for(OwnerAppointmentRequest request:ownerAppointmentRequests){
            if(user_to_appoint==request.getUserToAppoint())
                return true;
        }
        return false;
    }

    public void notifyProductsStaffChange(){
        for (Product prod: this.getProductsByName().values())
            prod.StaffUpdateNotify();
    }
}
