package main.Service;

import main.DTO.ProductDTO;
import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.DTO.UserDTO;
import main.Logger.Logger;
import main.Market;

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
        Logger.getInstance().logEvent("Service",String.format("Attempting to appoint store owner with parameters: token: %s userToAppoint: %s storeName:%s",userToken,userToAppoint,storeName));
        return market.appointStoreOwner(userToken,userToAppoint,storeName);
    }

    @Override
    public boolean removeStoreOwnerAppointment(String userToken, String userAppointed, String storeName) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to remove a store owner appointment parameters: token: %s userAppointed: %s storeName:%s",userToken,userAppointed,storeName));
        return market.removeStoreOwnerAppointment(userToken,userAppointed,storeName);
    }

    @Override
    public boolean appointStoreManager(String userToken, String userToAppoint, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to appoint store manager with parameters: token: %s userToAppoint: %s storeName:%s", userToken, userToAppoint, storeName));
        return market.appointStoreManager(userToken, userToAppoint, storeName);
    }

    @Override
    public boolean removeStoreManagerAppointment(String userToken, String userAppointed, String storeName) {
        return false;
    }

    @Override
    public boolean allowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant update products permission to:%s in store:%s",managerName,storeName));
        return market.allowManagerUpdateProducts(userToken,managerName,storeName);
    }

    @Override
    public boolean disAllowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow update products permission to:%s in store:%s",managerName,storeName));
        return market.disallowManagerUpdateProducts(userToken,managerName,storeName);
    }

    @Override
    public boolean allowManagerGetHistory(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant view purchase history permission to:%s in store:%s",managerName,storeName));
        return market.allowManagerViewPurchaseHistory(userToken,managerName,storeName);
    }

    @Override
    public boolean disAllowManagerGetHistory(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow viewing purchase history permission to:%s in store:%s",managerName,storeName));
        return market.disallowManagerViewPurchaseHistory(userToken,managerName,storeName);
    }

    @Override
    public boolean allowManagerViewStaffPermissions(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant view staff permissions permission to:%s in store:%s",managerName,storeName));
        return market.allowManagerViewStaffPermissions(userToken,managerName,storeName);
    }

    @Override
    public boolean disAllowManagerViewStaffPermissions(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow viewing staff permissions permission to:%s in store:%s",managerName,storeName));
        return market.disallowManagerViewStaffPermissions(userToken,managerName,storeName);
    }

    @Override
    public boolean allowManagerChangeStaffPermissions(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant change staff permissions permission to:%s in store:%s",managerName,storeName));
        return market.allowManagerChangeStaffPermissions(userToken,managerName,storeName);
    }

    @Override
    public boolean disAllowManagerChangeStaffPermissions(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow changing staff permissions permission to:%s in store:%s",managerName,storeName));
        return market.disallowManagerChangeStaffPermissions(userToken,managerName,storeName);
    }

    @Override
    public boolean allowManagerRemoveManager(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant remove manager permission to:%s in store:%s",managerName,storeName));
        return market.allowManagerRemoveManager(userToken,managerName,storeName);
    }

    @Override
    public boolean disAllowManagerRemoveManager(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow removing manager permission to:%s in store:%s",managerName,storeName));
        return market.disallowManagerRemoveManager(userToken,managerName,storeName);
    }

    @Override
    public boolean allowManagerAppointToManager(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant appoint manager permission to:%s in store:%s",managerName,storeName));
        return market.allowManagerAppointToManager(userToken,managerName,storeName);
    }

    @Override
    public boolean disAllowManagerAppointToManager(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow appointing manager permission to:%s in store:%s",managerName,storeName));
        return market.disallowManagerAppointToManager(userToken,managerName,storeName);
    }

    @Override
    public boolean allowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant answer and take requests permission to:%s in store:%s",managerName,storeName));
        return market.allowManagerAnswerAndTakeRequests(userToken,managerName,storeName);
    }

    @Override
    public boolean disAllowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow answer and take requests permission to:%s in store:%s",managerName,storeName));
        return market.disallowManagerAnswerAndTakeRequests(userToken,managerName,storeName);
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
