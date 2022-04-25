package main.Service;



import main.DTO.*;
import main.Logger.Logger;
import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Users.User;
import main.DTO.ProductDTO;
import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.DTO.UserDTO;
import main.Market;
import main.utils.Pair;
import main.utils.PaymentInformation;
import main.utils.Response;
import main.utils.SupplyingInformation;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class Service implements IService {

    private Market market;


    public Service(){
        market=new Market();
        market.initialize();

    }

    @Override

    public Response<String> guestConnect() {
        return new Response<>(market.ConnectGuest());
    }

    @Override
    public Response<UserDTO> guestDisconnect(String userToken) {
        try {
            User r = market.DisconnectGuest(userToken);
            return new Response<>(new UserDTO(r), null);
        } catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> register(String userName, String password) {
        try {
            market.Register(userName, password);
            return new Response<>(true);
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<UserDTO> login(String token, String userName, String password) {
        try {
            return new Response<>(new UserDTO(market.Login(token, userName, password)));
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> logout(String token) {
        try
        {
            market.logout(token);
            return new Response<>(true);
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch(Exception e){
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<StoreDTO> getStoreInfo(String storeName) {
        try {
            return new Response<>(new StoreDTO(market.getStoreByName(storeName)));
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e){
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<String>> getSmilliarStores(String storeName) {
        try {
            return new Response<>(market.getStoresByString(storeName));
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<ProductDTO>> getStoreProducts(String storeName) {
        try {
            List<ProductDTO> res = new LinkedList<>();
            for (Product p : market.getStoreProducts(storeName))
                res.add(new ProductDTO(p));
            return new Response<>(res);
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<ProductDTO>> getProductsByInfo(String productName, String category, String keyWord, Double productRating, Double storeRating, Double minPrice, Double maxPrice) {
        try {
            List<ProductDTO> res = new LinkedList<>();
            for (Product p : market.getProductsByAttributes(productName, category, keyWord, productRating, storeRating, minPrice, maxPrice))
                res.add(new ProductDTO(p));
            return new Response<>(res);
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> addProductToCart(String userToken, String storeName, String productName, int quantity) {
        try {
            return new Response<>(market.addProductToCart(userToken, storeName, productName, quantity));
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> RemoveProductFromCart(String userToken, String storeName, String productName, int quantity) {
        try {
            return new Response<>(market.RemoveProductFromCart(userToken, storeName, productName, quantity), null);
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<ShoppingCartDTO> getCartInfo(String userToken) {
        try {
            return new Response<>(new ShoppingCartDTO(market.getUserCart(userToken)), null);
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> purchaseCart(String userToken, PaymentInformation pi, SupplyingInformation si) {
        try
        {
            market.purchaseCart(userToken, pi, si);
            return new Response<>(true);
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch(Exception e)
        {
            return new Response<>(e, false);
        }

    }

    @Override

    public Response<Boolean> openStore(String userToken, String storeName) {
        try{
            return new Response<>(market.openStore(userToken,storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            return new Response<>(e,false);
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
    public Response<Boolean> sendQuestionsToStore(String userToken, String storeName, String message)
    {
        try
        {
            market.sendQuestionsToStore(userToken, storeName, message);
            return new Response<>(true, null);
        }
        catch (Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> sendComplaint(String userToken, String msg)
    {
        try
        {
            market.sendComplaint(userToken, msg);
            return new Response<>(true, null);
        }
        catch (Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<List<ShoppingCartDTO>> getPurchaseHistory(String userToken, String userName) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get purchase history, userToken:%s userName:%s",userToken,userName));
        try
        {
            List<ShoppingCartDTO> carts = market.getPurchaseHistory(userToken);
            return new Response<>(carts, null);
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch(Exception e)
        {
            Logger.getInstance().logBug("Service->getPurchaseHistory",e.getMessage());
            return new Response<>(e,false);
        }
    }


    @Override
    public Response<Boolean> addSecurityQuestion(String userToken, String question, String answer) {
        try
        {
            market.addSecurityQuestion(userToken, question, answer);
            return new Response<>(true, null);
        }
        catch (Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

    @Override
    public Response<Boolean> addProductToStore(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Logger.getInstance().logEvent("Service", String.format("Add product to store invoked with parameters: token: %s productName:%s storeName:%s", userToken, productName, storeName));
        try {
            boolean res = market.addProductToStore(userToken, productName, category, keyWords, description, storeName, quantity, price);
            return new Response<>(res);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - addProductToStore", "Bug in addProductToStore!");
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> removeProductFromStore(String userToken, String productName,String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Delete product from store invoked with parameters: token: %s productName:%s storeName:%s", userToken, productName, storeName));
        try{
            return new Response<>(market.removeProductFromStore(userToken,productName,storeName));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service - removeProductFromStore", "Bug in remove product");
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> updateProduct(String userToken, String oldProductName,String newProductName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Logger.getInstance().logEvent("Service", String.format("Update product to store invoked with parameters: token: %s productName:%s storeName:%s", userToken, oldProductName, storeName));
        try {
            return new Response<>(market.updateProductInStore(userToken, oldProductName,newProductName, category, keyWords, description, storeName, quantity, price));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - updateProduct", "Bug in update product");
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> appointStoreOwner(String userToken, String userToAppoint, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to appoint store owner with parameters: token: %s userToAppoint: %s storeName:%s", userToken, userToAppoint, storeName));
        try {
            return new Response<>(market.appointStoreOwner(userToken, userToAppoint, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - appointStoreOwner", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> removeStoreOwnerAppointment(String userToken, String userAppointed, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove a store owner appointment parameters: token: %s userAppointed: %s storeName:%s", userToken, userAppointed, storeName));
        try {
            return new Response<>(market.removeStoreOwnerAppointment(userToken, userAppointed, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - removeStoreOwnerAppointment", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> appointStoreManager(String userToken, String userToAppoint, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to appoint store manager with parameters: token: %s userToAppoint: %s storeName:%s", userToken, userToAppoint, storeName));
        try {
            return new Response<>(market.appointStoreManager(userToken, userToAppoint, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - appointStoreManager", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> removeStoreManagerAppointment(String userToken, String userAppointed, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove a store manager:%s from store:%s", userAppointed, storeName));
        try {
            return new Response<>(market.removeStoreManager(userToken, userAppointed, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - removeStoreManagerAppointment", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> allowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant update products permission to:%s in store:%s", managerName, storeName));
        try {
            return new Response<>(market.allowManagerUpdateProducts(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - allowManagerUpdateProducts", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> disAllowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow update products permission to:%s in store:%s", managerName, storeName));
        try {
            return new Response<>(market.disallowManagerUpdateProducts(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - disAllowManagerUpdateProducts", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> allowManagerGetHistory(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant view purchase history permission to:%s in store:%s", managerName, storeName));
        try {
            return new Response<>(market.allowManagerViewPurchaseHistory(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - allowManagerGetHistory", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> disAllowManagerGetHistory(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow viewing purchase history permission to:%s in store:%s", managerName, storeName));
        try {
            return new Response<>(market.disallowManagerViewPurchaseHistory(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Serivce - disAllowManagerGetHistory", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> allowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant answer and take requests permission to:%s in store:%s", managerName, storeName));
        try {
            return new Response<>(market.allowManagerAnswerAndTakeRequests(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - allowManagerAnswerAndTakeRequests", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> disAllowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow answer and take requests permission to:%s in store:%s", managerName, storeName));
        try {
            return new Response<>(market.disallowManagerAnswerAndTakeRequests(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - disAllowManagerAnswerAndTakeRequests", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> closeStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to close store:%s", storeName));
        try {
            return new Response<>(market.closeStore(userToken, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - closeStore", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> reopenStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to reopen store:%s", storeName));
        try {
            return new Response<>(market.reopenStore(userToken, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - reopenStore", e.getMessage());
            return new Response<>(e, false);
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
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - getStoreStaff", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<Pair<String, String>>> receiveQuestionsFromBuyers(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to receive questions from buyers from store:%s", storeName));
        try {
            return new Response<>(market.receiveQuestionsFromBuyers(userToken, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - receiveQuestionsFromBuyers", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> sendRespondToBuyers(String userToken, String storeName, String userToRespond, String msg) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to send respond from store:%s", storeName));
        try {
            return new Response<>(market.sendRespondToBuyer(userToken, storeName, userToRespond, msg));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - sendRespondToBuyers", e.getMessage());
            return new Response<>(e, false);
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
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);

        } catch (Exception e) {
            Logger.getInstance().logBug("Service - getStorePurchaseHistory", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> deleteStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove store %s", storeName));
        try {
            return new Response<>(market.deleteStore(userToken, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - deleteStore", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> deleteUser(String userToken, String userName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove user %s", userName));
        try {
            return new Response<>(market.deleteUser(userToken, userName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - deleteUser", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<String>> receiveMessages(String userToken) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get all messages for token: %s", userToken));
        try {
            return new Response<>(market.receiveMessages(userToken));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - receiveMessages", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> respondToMessage(String userToken, String userToRespond, String msg) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to respond to a message from %s", userToRespond));
        try {
            return new Response<>(market.respondToMessage(userToken, userToRespond, msg));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - respondToMessage", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<String> getNumberOfLoggedInUsersPerDate(String userToken, LocalDate date) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get system stats: logged in users per date: %s", date.toString()));
        try {
            return new Response<>(market.getNumberOfLoggedInUsersPerDate(userToken, date));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - getNumberOfLoggedInUsersPerDate", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<String> getNumberOfPurchasesPerDate(String userToken, LocalDate date) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get system stats: number of purchases per date: %s", date.toString()));
        try {
            return new Response<>(market.getNumberOfPurchasesPerDate(userToken, date));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - getNumberOfPurchasesPerDate", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<String> getNumberOfRegisteredUsersPerDate(String userToken, LocalDate date) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get system stats: registered users per date: %s", date.toString()));
        try {
            return new Response<>(market.getNumberOfRegisteredUsersPerDate(userToken, date));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - getNumberOfRegisteredUsersPerDate", e.getMessage());
            return new Response<>(e, false);
        }
    }

    public Response<Boolean> changePassword(String userToken, String oldPassword, String newPassword)
    {
        try
        {
            market.changePassword(userToken, oldPassword, newPassword);
            return new Response<>(true, null);
        }
        catch (Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

    public Response<Boolean> changeUsername(String userToken, String newUsername)
    {
        try
        {
            market.changeUsername(userToken, newUsername);
            return new Response<>(true, null);
        }
        catch (Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

    public Response<Boolean> isMemberLoggedOut(String userToken)
    {
        try
        {
            boolean res = market.isMemberLoggedOut(userToken);
            return new Response<>(res, null);
        }
        catch (Exception e)
        {
            return new Response<>(null, e.getMessage());
        }
    }

}
