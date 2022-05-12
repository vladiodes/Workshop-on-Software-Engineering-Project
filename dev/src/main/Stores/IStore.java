package main.Stores;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Publisher.Notification;
import main.Shopping.ShoppingBasket;
import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
import main.utils.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface IStore {

    List<User> getOwnersOfStore();

    List<User> getManagersOfStore();

    boolean addProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price);

    boolean updateProduct(String oldProductName, String newProductName, String category, List<String> keyWords, String description, int quantity, double price);

    ConcurrentLinkedQueue<OwnerPermissions> getOwnersAppointments();

    ConcurrentLinkedQueue<ManagerPermissions> getManagersAppointments();

    void addOwnerToStore(OwnerPermissions newOwnerAppointment);

    void addManager(ManagerPermissions newManagerAppointment);

    void removeManager(ManagerPermissions mp);

    void removeOwner(OwnerPermissions ow);

    void closeStore();

    ConcurrentHashMap<String, Product> getProductsByName();

    Product getProduct(String name);

    String getName();

    Boolean getIsActive();

    void reOpen();

    HashMap<User, String> getStoreStaff();

    boolean respondToBuyer(User toRespond, String msg);

    ConcurrentHashMap<ShoppingBasket, LocalDateTime> getPurchaseHistoryByTime();
    ConcurrentHashMap<ShoppingBasket, User> getPurchaseHistoryByUser();

    void CancelStaffRoles();

    boolean removeProduct(String productName);

    public void purchaseBasket(User user, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment, ShoppingBasket bask);

    void addReview(StoreReview sReview);

    void notifyBargainingStaff(Bid newbid);


    /***
     * @param productName name of product add discount to.
     * @param until discount is active until that date.
     * @param percent 0.3 (for instance) means 30% off.
     */
    void addDirectDiscount(String productName, LocalDate until, Double percent);
    void addSecretDiscount(String productName, LocalDate until, Double percent, String secretCode);
    void addConditionalDiscount(String productName, LocalDate until, HashMap<Restriction, Double> restrictions);

    void addRafflePolicy(String productName, Double price);
    void addAuctionPolicy(String productName, Double price, LocalDate until);

    void addNormalPolicy(String productName, Double price);

    boolean bidOnProduct(String productName, Bid bid);

    void addBargainPolicy(String productName, Double originalPrice);

    void sendMessageToStaffOfStore(Notification notification);

    List<String> getStoreMessages();

    void addQuestionToStore(String userName, String message);
}
