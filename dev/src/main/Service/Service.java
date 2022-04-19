package main.Service;

import main.DTO.*;
import main.Logger.Logger;
import main.Market;
import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Users.User;
import main.utils.Response;
import javax.naming.NoPermissionException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Service implements IService {

    private Market market;

    public Service() {
        market = new Market();
    }

    @Override

    public Response<String> guestConnect() {
        return new Response<>(market.ConnectGuest(), null);
    }

    @Override
    public Response<UserDTO> guestDisconnect(String userToken) {
        try {
            User r = market.DisconnectGuest(userToken);
            return new Response<>(new UserDTO(r), null);
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> register(String userName, String password) {
        try {
            market.Register(userName, password);
            return new Response<>(true, null);
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<UserDTO> login(String token, String userName, String password) {
        try {
            return new Response<>(new UserDTO(market.Login(token, userName, password)), null);
        } catch (Exception e) {
            return new Response<>(null, e.getMessage());
        }
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
    public Response<Boolean> addProductToStore(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Logger.getInstance().logEvent("Service", String.format("Add product to store invoked with parameters: token: %s productName:%s storeName:%s", userToken, productName, storeName));
        try{
            boolean res=market.addProductToStore(userToken, productName, category, keyWords, description, storeName, quantity, price);
            return new Response<>(res);
        }
        catch(IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in addProductToStore!");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> removeProductFromStore(String userToken, String productName) {
        throw new IllegalArgumentException("not implemented yet");
    }

    @Override
    public Response<Boolean> updateProduct(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Logger.getInstance().logEvent("Service", String.format("Update product to store invoked with parameters: token: %s productName:%s storeName:%s", userToken, productName, storeName));
        try{
            return new Response<>(market.updateProductInStore(userToken, productName, category, keyWords, description, storeName, quantity, price));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);

        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in update product");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> appointStoreOwner(String userToken, String userToAppoint, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to appoint store owner with parameters: token: %s userToAppoint: %s storeName:%s", userToken, userToAppoint, storeName));
        try{
            return new Response<>(market.appointStoreOwner(userToken, userToAppoint, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);

        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in appoint store owner");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> removeStoreOwnerAppointment(String userToken, String userAppointed, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove a store owner appointment parameters: token: %s userAppointed: %s storeName:%s", userToken, userAppointed, storeName));
        try{
            return new Response<>( market.removeStoreOwnerAppointment(userToken, userAppointed, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in remove store owner appointment");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> appointStoreManager(String userToken, String userToAppoint, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to appoint store manager with parameters: token: %s userToAppoint: %s storeName:%s", userToken, userToAppoint, storeName));
        try{
            return new Response<>(market.appointStoreManager(userToken, userToAppoint, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in appointStoreManager");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> removeStoreManagerAppointment(String userToken, String userAppointed, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove a store manager:%s from store:%s", userAppointed, storeName));
        try{
            return new Response<>(market.removeStoreManager(userToken, userAppointed, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in remove store manager appointment");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> allowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant update products permission to:%s in store:%s", managerName, storeName));
        try{
            return new Response<>(market.allowManagerUpdateProducts(userToken, managerName, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in allow manager update products");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> disAllowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow update products permission to:%s in store:%s", managerName, storeName));
        try{
            return new Response<>(market.disallowManagerUpdateProducts(userToken, managerName, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in disallow manager to update products");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> allowManagerGetHistory(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant view purchase history permission to:%s in store:%s", managerName, storeName));
        try{
            return new Response<>(market.allowManagerViewPurchaseHistory(userToken, managerName, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service", "Bug in allow manager get history");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> disAllowManagerGetHistory(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow viewing purchase history permission to:%s in store:%s", managerName, storeName));
        try{
            return new Response<>(market.disallowManagerViewPurchaseHistory(userToken, managerName, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e
            , true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Serivce","Bug in disallow manager get history");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> allowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant answer and take requests permission to:%s in store:%s", managerName, storeName));
        try{
            return new Response<>(market.allowManagerAnswerAndTakeRequests(userToken, managerName, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in allow manager answer and take requests");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> disAllowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow answer and take requests permission to:%s in store:%s", managerName, storeName));
        try{
            return new Response<>( market.disallowManagerAnswerAndTakeRequests(userToken, managerName, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in disallow manager answer and take requests");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> closeStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to close store:%s", storeName));
        try{
            return new Response<>(market.closeStore(userToken, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in close store");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> reOpenStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to reopen store:%s", storeName));
        try{
            return new Response<>(market.reOpenStore(userToken, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in reopen store");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<HashMap<UserDTO, String>> getStoreStaff(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to view store staff in store:%s", storeName));
        try {
            HashMap<User, String> map = market.getStoreStaff(userToken, storeName);

            HashMap<UserDTO, String> toReturn = new HashMap<>();
            for (User u : map.keySet()) {
                UserDTO dto = new UserDTO(u);
                toReturn.put(dto, map.get(u));
            }
            return new Response<>(toReturn);
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in get store staff");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<List<String>> receiveQuestionsFromBuyers(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to receive questions from buyers from store:%s", storeName));
        try{
            return new Response<>(market.receiveQuestionsFromBuyers(userToken, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in receive questions from buyers");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> sendRespondToBuyers(String userToken, String storeName, String userToRespond, String msg) {

        Logger.getInstance().logEvent("Service", String.format("Attempting to send respond from store:%s", storeName));
        try{
            return new Response<>(market.sendRespondToBuyer(userToken, storeName, userToRespond, msg));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in send respond to buyers");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<List<PurchaseDTO>> getStorePurchaseHistory(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get store%s purchase history", storeName));
        try {
            ConcurrentHashMap<ShoppingBasket, LocalDateTime> baskets = market.getStorePurchaseHistory(userToken, storeName);


            List<PurchaseDTO> output = new LinkedList<>();
            for (ShoppingBasket basket : baskets.keySet()) {
                HashMap<ProductDTO, Integer> products = new HashMap<>();
                for (Product product : basket.getProductsAndQuantities().keySet())
                    products.put(new ProductDTO(product), basket.getProductsAndQuantities().get(product));
                output.add(new PurchaseDTO(products, baskets.get(basket)));
            }
            return new Response<>(output);
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);

        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in get store purchase history");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> deleteStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove store %s", storeName));
        try{
            return new Response<>(market.deleteStore(userToken, storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in delete store");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> deleteUser(String userToken, String userName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove user %s", userName));
        try{
            return new Response<>(market.deleteUser(userToken, userName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);

        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in delete user");
            return new Response<>(e,false);

        }
    }

    @Override
    public Response<List<String>> receiveMessages(String userToken) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get all messages for token: %s", userToken));
        try{
            return new Response<>(market.receiveMessages(userToken));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in receive messages");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> respondToMessage(String userToken, String userToRespond, String msg) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to respond to a message from %s", userToRespond));
        try{
            return new Response<>(market.respondToMessage(userToken, userToRespond, msg));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","bug in respond to message");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<String> getNumberOfLoggedInUsersPerDate(String userToken, LocalDateTime date) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get system stats: logged in users per date: %s", date.toString()));
        try{
            return new Response<>( market.getNumberOfLoggedInUsersPerDate(userToken, date));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in get number of logged in users per date");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<String> getNumberOfPurchasesPerDate(String userToken, LocalDateTime date) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get system stats: number of purchases per date: %s", date.toString()));
        try{
            return new Response<>( market.getNumberOfPurchasesPerDate(userToken, date));
        }
         catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in get number of purchases per date");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<String> getNumberOfRegisteredUsersPerDate(String userToken, LocalDateTime date) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get system stats: registered users per date: %s", date.toString()));
        try{
            return new Response<>(market.getNumberOfRegisteredUsersPerDate(userToken, date));
        }
         catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service","Bug in get number of registered users per date");
            return new Response<>(e,false);
        }
    }
}
