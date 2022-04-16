package main.Service;


import main.DTO.*;
import main.utils.Pair;

import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface IService {

    /*
     ------------------------ Users,guests, purchases -------------------
     */
    /**
     * This function is invoked once a guest enters the system
     * @return returns a unique token that can be used by the system to identify the session of the guest
     * REQ 2.1.1
     */
    String guestConnect();

    /**
     * This function is invoked once a guest exits the system (all of his details are simply deleted)
     * @param userToken the token that is used by the system to identify the guest.
     * REQ 2.1.2
     */
    void guestDisconnect(String userToken);

    /**
     * Registration to the system
     * @return returns true/false upon successful registration
     * REQ 2.1.3
     */
    boolean register(String userName, String password);

    /**
     * Login to the system
     * @return returns a unique token that can be used by the system to identify the session of the user
     * REQ 2.1.4
     */
    String login(String userName, String password);

    /**
     * Logout of the system
     * @return true/false upon success or failure
     * REQ 2.3.1
     */
    boolean logout(String token);

    /**
     * REQ 2.2.1
     */
    StoreDTO getStoreInfo(String storeName);

    /**
     * REQ 2.2.1
     */
    List<ProductDTO>getStoreProducts(String storeName);

    /**
     * REQ 2.2.2
     */
    List<ProductDTO> getProductsByInfo(String productName, String category, String keyWord, double productRating, double storeRating, double minPrice, double maxPrice);

    /**
     * REQ 2.2.3
     * @return true/false upon success or failure
     */
    boolean addProductToBasket(String userToken,String storeName,String productName,int quantity);

    /**
     * REQ 2.2.4
     */
    boolean removeProductFromBasket(String userToken,String storeName,String productName,int quantity);

    /**
     * REQ 2.2.4
     */
    ShoppingCartDTO getCartInfo(String userToken);

    /**
     * REQ 2.2.5
     * User has to provide a credit card
     * @return true/false upon success/failure
     */
    boolean purchaseCart(String userToken,String cardNumber, int year, int month, int day, int cvv);

    /**
     * REQ 2.3.2
     * @return true/false upon success/failure
     */
    boolean openStore(String userToken,String storeName);

    /**
     * REQ 2.3.4
     * @return true/false upon success/failure
     */
    boolean writeReview(String userToken,String productName, String storeName, String reviewDescription, double points);

    /**
     * REQ 2.3.5
     * @return true/false upon success/failure
     */
    boolean sendQuestionsToStore(String userToken,String storeName,String message);

    /**
     * REQ 2.3.6
     * @return true/false upon success/failure
     */
    boolean sendComplaint(String userToken,String msg);

    /**
     * REQ 2.3.7
     * REQ 2.6.3
     */
    List<ShoppingCartDTO> getPurchaseHistory(String userToken,String userName);

    /**
     * REQ 2.3.8
     * @return true/false upon success/failure
     */
    boolean updateUserInfo(String userToken,String newUserName,String oldPassword,String newPassword);

    /**
     * REQ 2.3.9
     * @return true/false upon success/failure
     */
    boolean addSecurityQuestions(String userToken,String question,String answer);

    /*
     ------------------------ Stores, permissions -------------------
     */

    /**
     * REQ 2.4.1 - manage store inventory
     * REQ 2.5
     * @return true/false upon success/failure
     */
    boolean AddProductToStore(String userToken,String productName,String category,List<String>keyWords,String description,String storeName,int quantity, double price) throws NoPermissionException;

    /**
     * REQ 2.4.1 - manage store inventory
     * REQ 2.5
     * @return true/false upon success/failure
     */
    boolean updateProduct(String userToken,String productName,String category,List<String>keyWords,String description,String storeName,int quantity,double price) throws NoPermissionException;

    /**
     * REQ 2.4.4
     * @return true/false upon success/failure
     */
    boolean appointStoreOwner(String userToken,String userToAppoint,String storeName);

    /**
     * REQ 2.4.5
     * @return true/false upon success/failure
     */
    boolean removeStoreOwnerAppointment(String userToken,String userAppointed,String storeName);

    /**
     * REQ 2.4.6
     * @return true/false upon success/failure
     */
    boolean appointStoreManager(String userToken,String userToAppoint,String storeName);

    /**
     * REQ 2.4.8
     * @return true/false upon success/failure
     */
    boolean removeStoreManagerAppointment(String userToken,String userAppointed,String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    boolean allowManagerUpdateProducts(String userToken,String managerName,String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    boolean disAllowManagerUpdateProducts(String userToken,String managerName,String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    boolean allowManagerGetHistory(String userToken,String managerName,String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    boolean disAllowManagerGetHistory(String userToken,String managerName,String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    boolean allowManagerAnswerAndTakeRequests(String userToken,String managerName,String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    boolean disAllowManagerAnswerAndTakeRequests(String userToken,String managerName,String storeName);

    /**
     * REQ 2.4.9
     * @return true/false upon success/failure
     */
    boolean closeStore(String userToken,String storeName);

    /**
     * REQ 2.4.10
     * @return true/false upon success/failure
     */
    boolean reOpenStore(String userToken,String storeName);

    /**
     * REQ 2.4.11
     * @return a hash map in which the key-value pair is of the format <UserDTO,List<String>>:
     * UserDTO - represents the user that has a role in the store (manager,owner,founder)
     * List<String> - a list of all the permissions that the staff member has.
     */
    HashMap<UserDTO,String> getStoreStaff(String userToken,String storeName);

    /**
     * REQ 2.4.12
     * @return a collection of all the questions from all the buyers
     */
    List<String> receiveQuestionsFromBuyers(String userToken,String storeName);

    /**
     * REQ 2.4.12
     * @return true/false upon success/failure
     */
    boolean sendRespondToBuyers(String userToken,String storeName,String userToRespond,String msg);

    /**
     * REQ 2.4.13
     * REQ 2.5
     * REQ 2.6.4
     * @return a hashmap - the key is a pair of the purchase date and the integer is the quantity of the product that
     * was bought at that date
     * the value is the dto of the product.
     */
    List<PurchaseDTO> getStorePurchaseHistory(String userToken, String storeName);

    /*
     ------------------------ System manager actions -------------------
     */

    /**
     * REQ 2.6.1
     * deletes a store from the system - also takes off all the permissions that the
     * store staff had.
     * @return true/false upon success/failure
     */
    boolean deleteStore(String userToken, String storeName);

    /**
     * REQ 2.6.2
     * @return true/false upon success/failure
     */
    boolean deleteUser(String userToken,String userName);

    /**
     * REQ 2.6.3
     * @return a list of all the messages
     */
    List<String> receiveMessages(String userToken);

    /**
     * REQ 2.6.3
     * respond to messages
     * @return true/false upon success/failure
     */
    boolean respondToMessage(String userToken,String userToRespond,String msg);

    /*
     ------------------------ System stats -------------------
     These are all the functions to fulfil req 2.6.5 - a system manager can receive stats about the system.
     */

    /**
     * REQ 2.6.5
     */
    String getNumberOfLoggedInUsersPerDate(String userToken, LocalDateTime date);

    /**
     * REQ 2.6.5
     */
    String getNumberOfPurchasesPerDate(String userToken,LocalDateTime date);

    /**
     * REQ 2.6.5
     */
    String getNumberOfRegisteredUsersPerDate(String userToken,LocalDateTime date);
}
