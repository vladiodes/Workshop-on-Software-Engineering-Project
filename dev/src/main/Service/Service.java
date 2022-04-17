package main.Service;

import main.DTO.*;
import main.Logger.Logger;
import main.Market;
import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Users.User;
import main.utils.Pair;

import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove a store manager:%s from store:%s",userAppointed,storeName));
        return market.removeStoreManager(userToken,userAppointed,storeName);
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
        Logger.getInstance().logEvent("Service", String.format("Attempting to close store:%s",storeName));
        return market.closeStore(userToken,storeName);
    }

    @Override
    public boolean reOpenStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to reopen store:%s",storeName));
        return market.reOpenStore(userToken,storeName);
    }

    @Override
    public HashMap<UserDTO, String> getStoreStaff(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to view store staff in store:%s",storeName));
        HashMap<User,String> map=market.getStoreStaff(userToken,storeName);
        HashMap<UserDTO,String> toReturn=new HashMap<>();
        for(User u:map.keySet()){
            UserDTO dto = new UserDTO(u);
            toReturn.put(dto,map.get(u));
        }
        return toReturn;
    }

    @Override
    public List<String> receiveQuestionsFromBuyers(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to receive questions from buyers from store:%s",storeName));
        return market.receiveQuestionsFromBuyers(userToken,storeName);
    }

    @Override
    public boolean sendRespondToBuyers(String userToken, String storeName, String userToRespond, String msg) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to send respond from store:%s",storeName));
        return market.sendRespondToBuyer(userToken,storeName,userToRespond,msg);
    }

    @Override
    public List<PurchaseDTO> getStorePurchaseHistory(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get store%s purchase history",storeName));
        ConcurrentHashMap<ShoppingBasket,LocalDateTime> baskets=market.getStorePurchaseHistory(userToken,storeName);

        List<PurchaseDTO> output=new LinkedList<>();
        for(ShoppingBasket basket:baskets.keySet()){
            HashMap<ProductDTO,Integer> products=new HashMap<>();
            for(Product product:basket.getProductsAndQuantities().keySet())
                products.put(new ProductDTO(product),basket.getProductsAndQuantities().get(product));
            output.add(new PurchaseDTO(products,baskets.get(basket)));
        }
        return output;
    }

    @Override
    public boolean deleteStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove store %s",storeName));
        return market.deleteStore(userToken,storeName);
    }

    @Override
    public boolean deleteUser(String userToken, String userName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove user %s",userName));
        return market.deleteUser(userToken,userName);
    }

    @Override
    public List<String> receiveMessages(String userToken) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get all messages for token: %s",userToken));
        return market.receiveMessages(userToken);
    }

    @Override
    public boolean respondToMessage(String userToken, String userToRespond, String msg) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to respond to a message from %s",userToRespond));
        return market.respondToMessage(userToken,userToRespond,msg);
    }

    @Override
    public String getNumberOfLoggedInUsersPerDate(String userToken, LocalDateTime date) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get system stats: logged in users per date: %s",date.toString()));
        return market.getNumberOfLoggedInUsersPerDate(userToken,date);

    }

    @Override
    public String getNumberOfPurchasesPerDate(String userToken, LocalDateTime date) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get system stats: number of purchases per date: %s",date.toString()));
        return market.getNumberOfPurchasesPerDate(userToken,date);
    }

    @Override
    public String getNumberOfRegisteredUsersPerDate(String userToken, LocalDateTime date) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get system stats: registered users per date: %s",date.toString()));
        return market.getNumberOfRegisteredUsersPerDate(userToken,date);
    }
}
