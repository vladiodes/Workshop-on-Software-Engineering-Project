package main.Stores;

import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
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

    void purchaseBasket(NotificationBus bus,ShoppingBasket bask) throws Exception;

    void addReview(StoreReview sReview);

    boolean ValidateProduct(Product key, Integer value);
}
