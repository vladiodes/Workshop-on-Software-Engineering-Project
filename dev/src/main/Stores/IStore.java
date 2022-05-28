package main.Stores;

import main.DTO.ShoppingBasketDTO;
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

    ConcurrentHashMap<ShoppingBasketDTO, LocalDateTime> getPurchaseHistoryByTime();

    void CancelStaffRoles();

    boolean removeProduct(String productName);

    public void purchaseBasket(User user, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment, ShoppingBasket bask);

    void addReview(StoreReview sReview);

    void notifyBargainingStaff(Bid newbid);

    void addRafflePolicy(String productName, Double price);
    void addAuctionPolicy(String productName, Double price, LocalDate until);

    void addNormalPolicy(String productName, Double price);

    boolean bidOnProduct(String productName, Bid bid);

    void addBargainPolicy(String productName, Double originalPrice);

    void sendMessageToStaffOfStore(Notification notification);

    List<String> getStoreMessages();

    void addQuestionToStore(String userName, String message);

    /***
     * @param until discounts can last until a certain date or LocalDate.MAX (forever)
     * @param percent 0.3 -> 30% off
     * @return id of the discount.
     */
    int CreateSimpleDiscount(LocalDate until, Double percent);
    int CreateSecretDiscount(LocalDate until, Double percent, String secretCode);
    int CreateConditionalDiscount(LocalDate until, Double percent, int condID);
    int CreateMaximumCompositeDiscount(LocalDate until, List<Integer> discounts);
    int CreatePlusCompositeDiscount(LocalDate until, List<Integer> discounts);
    /***
     *
     * @param -1 to remove.
     */
    void SetDiscountToProduct(int discountID, String productName);
    /***
     *
     * @param -1 to remove.
     */
    void SetDiscountToStore(int discountID);

    /***
     * @return condition's ID.
     */
    int CreateBasketValueCondition(double requiredValue);
    int CreateCategoryAmountCondition(String category, int amount);
    int CreateProductAmountCondition(String productName, int amount);
    int CreateLogicalAndCondition(List<Integer> conditionIds);
    int CreateLogicalOrCondition(List<Integer> conditionIds);
    int CreateLogicalXorCondition(int id1, int id2);
    void SetConditionToDiscount(int discountId, int ConditionID);

    /***
     *
     * @param -1 to remove.
     */
    void SetConditionToStore(int ConditionID);


    double getPriceForProduct(Product product, User user);

    boolean ValidateBasket(User user, ShoppingBasket shoppingBasket);

    boolean isProductAddable(String productName);
}
