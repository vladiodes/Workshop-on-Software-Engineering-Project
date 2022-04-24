package main.Service;



import main.DTO.*;
import main.utils.Pair;
import main.DTO.ProductDTO;
import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.DTO.UserDTO;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface IService {

    /*
     ------------------------ Users,guests, purchases -------------------
     */
    /**
     * This function is invoked once a guest enters the system.
     * Should be invoked on every system startup.
     * @return returns a unique token that can be used by the system to identify the session of the guest
     * REQ 2.1.1
     */
    Response<String> guestConnect();

    /**
     * This function is invoked once a guest exits the system (all of his details are simply deleted)
     * @param userToken the token that is used by the system to identify the guest.
     * REQ 2.1.2
     * @return a user DTO
     */
    Response<UserDTO> guestDisconnect(String userToken);

    /**
     * Registration to the system
     * @return returns true/false upon successful registration
     * REQ 2.1.3
     */
    Response<Boolean> register(String userName, String password);

    /**
     * Login to a member in the system.
     * @return returns a unique token that can be used by the system to identify the session of the user
     * REQ 2.1.4
     */
    Response<UserDTO> login(String token, String userName, String password);

    /**
     * Logout of the system
     * @return true/false upon success or failure
     * REQ 2.3.1
     */
    Response<Boolean> logout(String token);

    /**
     * REQ 2.2.1
     */
    Response<StoreDTO> getStoreInfo(String storeName);


    /**
     * REQ 2.2.1
     */
    Response<List<String>> getSmilliarStores(String storeName);

    /**
     * REQ 2.2.1
     */
    Response<List<ProductDTO>>getStoreProducts(String storeName);

    /**
     * does bitwise & between the conditions.
     * null means to ignore a certain condition.
     * REQ 2.2.2
     */
    Response<List<ProductDTO>> getProductsByInfo(String productName, String category, String keyWord, Double productRating, Double storeRating, Double minPrice, Double maxPrice);

    /**
     * REQ 2.2.3
     * @return true/false upon success or failure
     */
    Response<Boolean> addProductToCart(String userToken, String storeName, String productName, int quantity);


    /**
     * REQ 2.2.4
     */
    Response<Boolean> RemoveProductFromCart(String userToken, String storeName, String productName, int quantity);


    /**
     * REQ 2.2.4
     */
    Response<ShoppingCartDTO> getCartInfo(String userToken);

    /**
     * REQ 2.2.5
     * User has to provide a credit card
     * @return true/false upon success/failure
     */
    Response<Boolean> purchaseCart(String userToken, PaymentInformation pi, SupplyingInformation si);

    /**
     * REQ 2.3.2
     * @return true/false upon success/failure
     */
    Response<Boolean> openStore(String userToken, String storeName);


    /**
     * REQ 2.3.4
     * @return true/false upon success/failure
     */
    Response<Boolean> writeProductReview(String userToken,String productName, String storeName, String reviewDescription, double points);

    /**
     * REQ 2.3.4
     * @return true/false upon success/failure
     */
    Response<Boolean> writeStoreReview(String userToken, String storeName, String reviewDescription, double points);

    /**
     * REQ 2.3.5
     * @return true/false upon success/failure
     */
    Response<Boolean> sendQuestionsToStore(String userToken, String storeName, String message);

    /**
     * REQ 2.3.6
     * Sends a complaint to one of the market admins
     * @return true/false upon success/failure
     */
    Response<Boolean> sendComplaint(String userToken, String msg);

    /**
     * REQ 2.3.7
     * @return true/false upon success/failure
     */
    Response<Boolean> changePassword(String userToken, String oldPassword, String newPassword);

    /**
     * REQ 2.3.7
     * @return true/false upon success/failure
     */
    Response<Boolean> changeUsername(String userToken, String newUsername);

    /**
     * REQ 2.3.7
     * REQ 2.6.3
     * @param userToken - the user that invokes the action
     * @param userName - the user to check its history - admin can check for any user, non admin can only check for itself
     */
    Response<List<ShoppingCartDTO>> getPurchaseHistory(String userToken,String userName);

    /**
     * REQ 2.3.8
     * @return true/false upon success/failure
     */
    boolean updateUserInfo(String userToken, String newUserName, String oldPassword, String newPassword);

    /**
     * REQ 2.3.9
     * @return true/false upon success/failure
     */
    Response<Boolean> addSecurityQuestion(String userToken,String question,String answer);

    /*
     ------------------------ Stores, permissions -------------------
     */

    /**
     * REQ 2.4.1 - manage store inventory
     * REQ 2.5
     * @return true/false upon success/failure
     */
    Response<Boolean> addProductToStore(String userToken, String productName, String category, List<String>keyWords, String description, String storeName, int quantity, double price);

    /**
     * REQ 2.4.1 - manage store inventory
     * REQ 2.5
     * @return true/false upon success/failure
     */
    Response<Boolean> removeProductFromStore(String userToken, String productName,String storeName) ;

    /**
     * REQ 2.4.1 - manage store inventory
     * REQ 2.5
     * @return true/false upon success/failure
     */
    Response<Boolean> updateProduct(String userToken, String oldProductName,String newProductName, String category, List<String> keyWords, String description, String storeName, int quantity, double price);

    /**
     * REQ 2.4.4
     * @return true/false upon success/failure
     */
    Response<Boolean> appointStoreOwner(String userToken,String userToAppoint,String storeName);

    /**
     * REQ 2.4.5
     * @return true/false upon success/failure
     */
    Response<Boolean> removeStoreOwnerAppointment(String userToken, String userAppointed, String storeName);

    /**
     * REQ 2.4.6
     * @return true/false upon success/failure
     */
    Response<Boolean> appointStoreManager(String userToken, String userToAppoint, String storeName);

    /**
     * REQ 2.4.8
     * @return true/false upon success/failure
     */
    Response<Boolean> removeStoreManagerAppointment(String userToken, String userAppointed, String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    Response<Boolean> allowManagerUpdateProducts(String userToken, String managerName, String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    Response<Boolean> disAllowManagerUpdateProducts(String userToken, String managerName, String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    Response<Boolean> allowManagerGetHistory(String userToken, String managerName, String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    Response<Boolean> disAllowManagerGetHistory(String userToken, String managerName, String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    Response<Boolean> allowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName);

    /**
     * REQ 2.4.7
     * @return true/false upon success/failure
     */
    Response<Boolean> disAllowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName);

    /**
     * REQ 2.4.9
     * @return true/false upon success/failure
     */
    Response<Boolean> closeStore(String userToken, String storeName);

    /**
     * REQ 2.4.10
     * @return true/false upon success/failure
     */
    Response<Boolean> reopenStore(String userToken, String storeName);

    /**
     * REQ 2.4.11
     * @return a hash map in which the key-value pair is of the format <UserDTO,List<String>>:
     * UserDTO - represents the user that has a role in the store (manager,owner,founder)
     * List<String> - a list of all the permissions that the staff member has.
     */
    Response<HashMap<UserDTO,String>> getStoreStaff(String userToken, String storeName);

    /**
     * REQ 2.4.12
     * @return a collection of all the questions from all the buyers
     */
    Response<List<Pair<String,String>>> receiveQuestionsFromBuyers(String userToken, String storeName);

    /**
     * REQ 2.4.12
     * @return true/false upon success/failure
     */
    Response<Boolean> sendRespondToBuyers(String userToken, String storeName, String userToRespond, String msg);

    /**
     * REQ 2.4.13
     * REQ 2.5
     * REQ 2.6.4
     * @return a hashmap - the key is a pair of the purchase date and the integer is the quantity of the product that
     * was bought at that date
     * the value is the dto of the product.
     */
    Response<List<PurchaseDTO>> getStorePurchaseHistory(String userToken, String storeName);

    /*
     ------------------------ System manager actions -------------------
     */

    /**
     * REQ 2.6.1
     * deletes a store from the system - also takes off all the permissions that the
     * store staff had.
     * @return true/false upon success/failure
     */
    Response<Boolean> deleteStore(String userToken, String storeName);

    /**
     * REQ 2.6.2
     * @return true/false upon success/failure
     */
    Response<Boolean> deleteUser(String userToken, String userName);

    /**
     * REQ 2.6.3
     * Also - a connected user will use this function to get all of its pending messages
     * @return a list of all the messages
     */
    Response<List<String>> receiveMessages(String userToken);

    /**
     * REQ 2.6.3
     * respond to messages
     * @param userToken - the responding user
     * @param userToRespond - the user to send the message to
     * @param msg - the message itself
     * @return true/false upon success/failure
     */
    Response<Boolean> respondToMessage(String userToken, String userToRespond, String msg);

    /*
     ------------------------ System stats -------------------
     These are all the functions to fulfil req 2.6.5 - a system manager can receive stats about the system.
     */

    /**
     * REQ 2.6.5
     */
    Response<String> getNumberOfLoggedInUsersPerDate(String userToken, LocalDate date);

    /**
     * REQ 2.6.5
     */
    Response<String> getNumberOfPurchasesPerDate(String userToken, LocalDate date);

    /**
     * REQ 2.6.5
     */
    Response<String> getNumberOfRegisteredUsersPerDate(String userToken, LocalDate date);
}
