package main.Service;

import main.DTO.ProductDTO;
import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.DTO.UserDTO;
import main.Logger.Logger;
import main.Market;
import main.Stores.IStore;
import main.Users.User;

import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class Service implements IService{

    private Market market;

    public Service(){
        market=new Market();
    }


    @Override
    public String guestConnect() {
        return null;
    }

    @Override
    public void guestDisconnect(String userToken) {

    }

    @Override
    public boolean register(String userName, String password) {
        return false;
    }

    @Override
    public String login(String userName, String password) {
        return null;
    }

    @Override
    public boolean logout(String token) {
        return false;
    }

    @Override
    public StoreDTO getStoreInfo(String storeName) {
        return null;
    }

    @Override
    public List<ProductDTO> getStoreProducts(String storeName) {
        return null;
    }

    @Override
    public List<ProductDTO> getProductsByInfo(String productName, String category, String keyWord, double productRating, double storeRating, double minPrice, double maxPrice) {
        return null;
    }

    @Override
    public boolean addProductToBasket(String userToken, String storeName, String productName, int quantity) {
        return false;
    }

    @Override
    public boolean removeProductFromBasket(String userToken, String storeName, String productName, int quantity) {
        return false;
    }

    @Override
    public ShoppingCartDTO getCartInfo(String userToken) {
        return null;
    }

    @Override
    public boolean purchaseCart(String userToken, String cardNumber, int year, int month, int day, int cvv) {
        return false;
    }

    @Override
    public boolean openStore(String userToken, String storeName) {
        return false;
    }

    @Override
    public boolean writeReview(String userToken, String productName, String storeName, String reviewDescription, double points) {
        return false;
    }

    @Override
    public boolean sendQuestionsToStore(String userToken, String storeName, String message) {
        return false;
    }

    @Override
    public boolean sendComplaint(String userToken, String msg) {
        return false;
    }

    @Override
    public List<ShoppingCartDTO> getPurchaseHistory(String userToken, String userName) {
        return null;
    }

    @Override
    public boolean updateUserInfo(String userToken, String newUserName, String oldPassword, String newPassword) {
        return false;
    }

    @Override
    public boolean addSecurityQuestions(String userToken, String question, String answer) {
        return false;
    }

    @Override
    public boolean AddProductToStore(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity,double price) throws NoPermissionException {
        Logger.getInstance().logEvent("Service", String.format("Add product to store invoked with parameters: token: %s productName:%s storeName:%s", userToken, productName, storeName));
        return market.addProductToStore(userToken, productName, category, keyWords, description, storeName, quantity, price);
    }

    @Override
    public boolean updateProduct(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity,double price) throws NoPermissionException {
        Logger.getInstance().logEvent("Service",String.format("Update product to store invoked with parameters: token: %s productName:%s storeName:%s",userToken,productName,storeName));
        return market.updateProductInStore(userToken,productName,category,keyWords,description,storeName,quantity,price);
    }

    @Override
    public boolean appointStoreOwner(String userToken, String userToAppoint, String storeName) {
        return false;
    }

    @Override
    public boolean removeStoreOwnerAppointment(String userToken, String userAppointed, String storeName) {
        return false;
    }

    @Override
    public boolean appointStoreManager(String userToken, String userToAppoint, String storeName) {
        return false;
    }

    @Override
    public boolean removeStoreManagerAppointment(String userToken, String userAppointed, String storeName) {
        return false;
    }

    @Override
    public boolean allowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        return false;
    }

    @Override
    public boolean disAllowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        return false;
    }

    @Override
    public boolean allowManagerGetHistory(String userToken, String managerName, String storeName) {
        return false;
    }

    @Override
    public boolean disAllowManagerGetHistory(String userToken, String managerName, String storeName) {
        return false;
    }

    @Override
    public boolean closeStore(String userToken, String storeName) {
        return false;
    }

    @Override
    public boolean reOpenStore(String userToken, String storeName) {
        return false;
    }

    @Override
    public HashMap<UserDTO, List<String>> getStoreStaff(String userToken, String storeName) {
        return null;
    }

    @Override
    public List<String> receiveQuestionsFromBuyers(String userToken, String storeName) {
        return null;
    }

    @Override
    public boolean sendRespondToBuyers(String userToken, String storeName, String userToRespond, String msg) {
        return false;
    }

    @Override
    public List<ProductDTO> getStorePurchaseHistory(String userToken, String storeName) {
        return null;
    }

    @Override
    public boolean deleteStore(String userToken, String storeName) {
        return false;
    }

    @Override
    public boolean deleteUser(String userToken, String userName) {
        return false;
    }

    @Override
    public List<String> receiveMessages(String userToken) {
        return null;
    }

    @Override
    public boolean respondToMessage(String userToken, String userToRespond, String msg) {
        return false;
    }

    @Override
    public String getNumberOfLoggedInUsersPerDate(String userToken, LocalDateTime date) {
        return null;
    }

    @Override
    public String getNumberOfPurchasesPerDate(String userToken, LocalDateTime date) {
        return null;
    }

    @Override
    public String getNumberOfRegisteredUsersPerDate(String userToken, LocalDateTime date) {
        return null;
    }
}
