package main.Users;

import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;
import main.Stores.IStore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public interface IUser {
    ShoppingCart getCart();

    String getUserName();

    String getHashed_password();

    void LogIn();

    Boolean getIsLoggedIn();

    boolean addProductToStore(IStore IStore, String productName, String category, List<String> keyWords, String description, int quantity, double price);

    boolean updateProductToStore(IStore IStore, String productName, String category, List<String> keyWords, String description, int quantity, double price);

    boolean appointOwnerToStore(IStore IStore, User user_to_appoint);

    boolean removeOwnerAppointment(IStore IStore, User appointed_user);

    boolean removeManagerAppointment(IStore IStore, User manager);

    boolean appointManagerToStore(IStore IStore, User user_to_appoint);

    boolean grantOrDeletePermission(User manager, IStore IStore, boolean shouldGrant, StorePermission permission);

    boolean closeStore(IStore IStore, NotificationBus bus);

    boolean reOpenStore(IStore IStore, NotificationBus bus);

    HashMap<User, String> getStoreStaff(IStore IStore);

    List<String> receiveQuestionsFromStore(IStore IStore);

    boolean sendRespondFromStore(IStore IStore, User toRespond, String msg, NotificationBus bus);

    ConcurrentHashMap<ShoppingBasket, LocalDateTime> getStorePurchaseHistory(IStore IStore);

    boolean removeStore(IStore IStore);

    void removeFounderRole(IStore IStore);

    void removeOwnerRole(OwnerPermissions ownerPermissions);

    void removeManagerRole(ManagerPermissions managerPermissions);

    boolean deleteUser(User toDelete);

    boolean isAdmin();

    IStore openStore(String storeName);

    boolean removeProductFromStore(String productName, IStore IStore);

    boolean addProductToCart(IStore st, String productName, int quantity);

    boolean RemoveProductFromCart(IStore st, String productName, int quantity);

    List<IStore> getFoundedIStores();
}
