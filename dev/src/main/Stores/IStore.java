package main.Stores;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.Restriction;
import main.utils.SupplyingInformation;
import org.mockito.internal.matchers.Not;

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

    void closeStore(NotificationBus bus);

    ConcurrentHashMap<String, Product> getProductsByName();

    Product getProduct(String name);

    String getName();

    Boolean getIsActive();

    void reOpen(NotificationBus bus);

    HashMap<User, String> getStoreStaff();

    boolean respondToBuyer(User toRespond, String msg, NotificationBus bus);

    ConcurrentHashMap<ShoppingBasket, LocalDateTime> getPurchaseHistory();

    void CancelStaffRoles();

    boolean removeProduct(String productName);

    public void purchaseBasket(User user, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment, NotificationBus bus, ShoppingBasket bask);

    void addReview(StoreReview sReview);


    /***
     * @param productName name of product add discount to.
     * @param until discount is active until that date.
     * @param percent 0.3 (for instance) means 30% off.
     */
    void addDirectDiscount(String productName, LocalDate until, Double percent);
    void addSecretDiscount(String productName, LocalDate until, Double percent, String secretCode);
    void addConditionalDiscount(String productName, LocalDate until, HashMap<Restriction, Double> restrictions);

    void addRafflePolicy(String productName, Double price, NotificationBus bus);
    void addAuctionPolicy(String productName, Double price, NotificationBus bus, LocalDate until);

    void addNormalPolicy(String productName, Double price, NotificationBus bus);

    void bidOnProduct(String productName, Bid bid);
}
