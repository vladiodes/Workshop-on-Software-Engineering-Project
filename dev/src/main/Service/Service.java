package main.Service;

import main.DTO.ProductDTO;
import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.DTO.UserDTO;
import main.Logger.Logger;
import main.Market;
import main.Shopping.ShoppingCart;
import main.Stores.Product;
import main.Stores.Store;
import main.Users.User;
import main.utils.Response;

import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Service implements IService{

    private Market market;


    public Service(){
        market=new Market();
    }


    @Override
    public Response<String> guestConnect() {
        return new Response(market.ConnectGuest(), null);
    }

    @Override
    public Response<UserDTO> guestDisconnect(String userToken) {
        try {
            User r = market.DisconnectGuest(userToken);
            return new Response<>(new UserDTO(r), null);
        }
        catch (Exception e){
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> register(String userName, String password) {
        try {
            market.Register(userName, password);
            return new Response<>(true, null);
        }
        catch (Exception e){
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<UserDTO> login(String token, String userName, String password) {
        try {
            return new Response<>(new UserDTO(market.Login(token, userName, password)),null);
        }
        catch (Exception e){
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> logout(String token) {
        try
        {
            market.logout(token);
            return new Response<>(true, null);
        }
        catch(Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<StoreDTO> getStoreInfo(String storeName) {
        try {
            return new Response<>(new StoreDTO(market.getStoreByName(storeName)),null);
        }
        catch (Exception e){
            return new Response<>(null, e.getMessage());
        }
    }


    @Override
    public Response<List<String>> getSmilliarStores(String storeName) {
        try {
            return new Response<>(market.getStoresByString(storeName), null);
        }
        catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<List<ProductDTO>> getStoreProducts(String storeName) {
        try {
            List<ProductDTO> res = new LinkedList<>();
            for (Product p : market.getStoreProducts(storeName))
                res.add(new ProductDTO(p));
            return new Response<>(res, null);
        }
        catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<List<ProductDTO>> getProductsByInfo(String productName, String category, String keyWord, Double productRating, Double storeRating, Double minPrice, Double maxPrice) {
        try {
            List<ProductDTO> res = new LinkedList<>();
            for (Product p : market.getProductsByAttributes(productName, category, keyWord, productRating, storeRating, minPrice, maxPrice))
                res.add(new ProductDTO(p));
            return new Response<>(res, null);
        }
        catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
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
    public Response<Boolean> purchaseCart(String userToken, String cardNumber, int year, int month, int day, int cvv) {
        try
        {
            market.purchaseCart(userToken, cardNumber, year, month, day, cvv);
            return new Response<>(true, null);
        }
        catch(Exception e)
        {
            return new Response<>(null, e.getMessage());
        }

    }

    @Override
    public Response<StoreDTO> openStore(String userToken, String storeName){
        try
        {
            Store s = market.openStore(userToken, storeName);
            return new Response<>(new StoreDTO(s), null);
        }
        catch(Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> writeProductReview(String userToken, String productName, String storeName, String reviewDescription, double points) {
        try
        {
            market.writeProductReview(userToken, productName, storeName, reviewDescription, points);
            return new Response<>(true, null);
        }
        catch(Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> writeStoreReview(String userToken, String storeName, String reviewDescription, double points) {
        try
        {
            market.writeStoreReview(userToken, storeName, reviewDescription, points);
            return new Response<>(true, null);
        }
        catch(Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
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
    public Response<List<ShoppingCartDTO>> getPurchaseHistory(String userToken, String userName) {
        try
        {
            List<ShoppingCartDTO> carts = market.getPurchaseHistory(userToken);
            return new Response<>(carts, null);
        }
        catch(Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public boolean updateUserInfo(String userToken, String newUserName, String oldPassword, String newPassword) {
        return false;
    }

    @Override
    public Response<Boolean> addSecurityQuestion(String userToken, String question, String answer) {
        try
        {
            market.addSecurityQuestion(userToken, question, answer);
            return new Response<Boolean>(true, null);
        }
        catch (Exception e)
        {
            return new Response<Boolean>(null, e.getMessage());
        }
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
