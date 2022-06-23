package main.Service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.log.Log;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import main.DTO.*;
import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Logger.Logger;
import main.Market.MarketBuilder;
import main.Persistence.DAO;
import main.Service.CommandExecutor.Commands.*;
import main.Service.CommandExecutor.Invoker;
import main.Service.CommandExecutor.utils.UserTokens;
import main.Users.User;
import main.DTO.ProductDTO;
import main.DTO.ShoppingCartDTO;
import main.DTO.StoreDTO;
import main.DTO.UserDTO;
import main.Market.Market;
import main.utils.*;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public class Service implements IService {

    private Market market;
    private boolean logCommandsFlag = false;
    private UserTokens uTokens = new UserTokens();
    private String logFileName = "DefaultVladi.json";
    private List<Invoker<?>> commands;
    public Service(IPayment Psystem, ISupplying Isystem) throws Exception{
        MarketBuilder builder = new MarketBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        Configuration conf = objectMapper.readValue(Paths.get("TestingConfig.json").toFile(), Configuration.class);
        builder.setPSystem(Psystem);
        builder.setSSystem(Isystem);
        market=builder.build(conf);
    }

    public UserTokens getuTokens() {
        return uTokens;
    }

    public Service(String configFileLocation) throws Exception {
        MarketBuilder builder = new MarketBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        Configuration conf = objectMapper.readValue(Paths.get(configFileLocation).toFile(), Configuration.class);
        market=builder.build(conf);
    }

    public <T> void logCommand(Class<? extends Command<T>> cl,Command<T> command){
        if(!logCommandsFlag)
            return;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String commJson = objectMapper.writeValueAsString(command);
            Invoker<?> invoker = new Invoker<>(cl, commJson);
            this.commands.add(invoker);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service",String.format("Failed to log command %s", e.getMessage()));
        }
    }

    public void setLogCommandsFlag(boolean logCommandsFlag) {
        if(logCommandsFlag)
            commands = new ArrayList<>();
        this.logCommandsFlag = logCommandsFlag;
    }

    public void SaveRecording(){
        if(!logCommandsFlag)
            return;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File(this.logFileName), commands);
        }
        catch (Exception e){
            System.out.println(String.format("Couldn't save recording: %s", e.getMessage()));
        }
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    @Override

    public Response<String> guestConnect() {
        Logger.getInstance().logEvent("Service",String.format("Attempting to connect a guest"));
        Response<String> output = new Response<>(market.ConnectGuest());
        Logger.getInstance().logGuest(output.getResult());
        this.logCommand(guestConnectCommand.class, new guestConnectCommand(this.uTokens, output.getResult()));
        return output;
    }

    @Override
    public Response<UserDTO> guestDisconnect(String userToken) {
        try {
            Logger.getInstance().logEvent("Service",String.format("Attempting to disconnect a guest, userToken:%s" ,userToken));
            UserDTO r = market.DisconnectGuest(userToken);
            this.logCommand(guestDisconnectCommand.class, new guestDisconnectCommand(this.uTokens, userToken));
            return new Response<>(r, null);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - guestDisconnect", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> register(String userName, String password) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to register, userName:%s",userName));
        try {
            DAO.getInstance().openTransaction();
            market.Register(userName, password);
            logCommand(registerCommand.class, new registerCommand(userName, password));
            return new Response<>(true);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to register, userName:%s, Error:%s" ,userName, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service - register", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<UserDTO> login(String token, String userName, String password) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get login, userName:%s", userName));
        try {
            DAO.getInstance().openTransaction();
            UserDTO u = market.Login(token, userName, password);
            logCommand(loginCommand.class, new loginCommand(this.uTokens, token, userName, password));
            return new Response<>(u);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to login, userToken:%s, Error:%s" ,token, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service - login", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> logout(String token) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to logout, userToken:%s" ,token));
        try
        {
            DAO.getInstance().openTransaction();
            market.logout(token);
            Logger.getInstance().logEvent("Service",String.format("Logged out successfully, userToken:%s" ,token));
            logCommand(logoutCommand.class, new logoutCommand(this.uTokens, token));
            return new Response<>(true);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to logout, userToken:%s, Error:%s" ,token, e.getMessage()));
            return new Response<>(e, true);
        }
        catch(Exception e){
            Logger.getInstance().logBug("Service - logout", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<StoreDTO> getStoreInfo(String storeName) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get store info, storeName:%s" ,storeName));
        try {
            StoreDTO s = market.getStoreByName(storeName);
            return new Response<>(s);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to get store info, storeName:%s, Error:%s" ,storeName, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service - getStoreInfo", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<String>> getSmilliarStores(String storeName) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get similliar stores, storeName:%s" ,storeName));
        try {
            return new Response<>(market.getStoresByString(storeName));
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to get similliar sores, storeName:%s, Error:%s" ,storeName, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e) {
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<ProductDTO>> getStoreProducts(String storeName) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get store products, storeName:%s" ,storeName));
        try {
            List<ProductDTO> res = market.getStoreProducts(storeName);
            return new Response<>(res);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to get store products, storeName:%s, Error:%s" ,storeName, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service - getStoreProducts", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<ProductDTO>> getProductsByInfo(String productName, String category, String keyWord, Double productRating, Double storeRating, Double minPrice, Double maxPrice) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get product by info, productName:%s, category:%s, keyWord:%s, productRating:%.2f, storeRating:%.2f, minPrice:%.2f, maxPrice:%.2f" ,productName,category, keyWord,productRating,storeRating, minPrice, maxPrice));
        try {
            List<ProductDTO> res = market.getProductsByAttributes(productName, category, keyWord, productRating, storeRating, minPrice, maxPrice);
            return new Response<>(res);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to get product by info, productName:%s, category:%s, keyWord:%s, productRating:%.2f, storeRating:%.2f, minPrice:%.2f, maxPrice:%.2f, Error:%s" ,productName,category, keyWord,productRating,storeRating, minPrice, maxPrice, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service - getProductsByInfo", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> addProductToCart(String userToken, String storeName, String productName, int quantity) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to add product to cart, userToken:%s storeName:%s productName:%s quantity:%d",userToken,storeName, productName, quantity));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.addProductToCart(userToken, storeName, productName, quantity));
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to add product to cart, userToken:%s, storeName:%s, productName:%s, quantity:%d, Error:%s" ,userToken, storeName, productName, quantity, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service - addProductToCart", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> setCostumPriceForProductInCart(String userToken, String storeName, String productName, double price) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.addProductToCart(userToken, storeName, productName, price));
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service - addProductToCart", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> RemoveProductFromCart(String userToken, String storeName, String productName, int quantity) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to remove product from cart, userToken:%s storeName:%s productName:%s quantity:%d",userToken,storeName, productName, quantity));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.RemoveProductFromCart(userToken, storeName, productName, quantity), null);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to add product to cart, userToken:%s, storeName:%s, productName:%s, quantity:%d, Error:%s" ,userToken, storeName, productName, quantity, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service - removeProductFromCart", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<ShoppingCartDTO> getCartInfo(String userToken) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get cart info, userToken:%s" ,userToken));
        try {
            return new Response<>(market.getUserCart(userToken));
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to get cart info, userToken:%s, Error:%s" ,userToken, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service - getCartInfo", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> purchaseCart(String userToken, PaymentInformation pi, SupplyingInformation si) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to purchase cart, userToken:%s" ,userToken));
        try
        {
            DAO.getInstance().openTransaction();
            market.purchaseCart(userToken, pi, si);
            return new Response<>(true);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to purchase cart, userToken:%s, Error:%s" ,userToken, e.getMessage()));
            return new Response<>(e, true);
        }
        catch(Exception e)
        {
            Logger.getInstance().logBug("Service - purchaseCart", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }

    }

    @Override

    public Response<Boolean> openStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to open store, userToken:%s storeName:%s" ,userToken,storeName));
        try{
            DAO.getInstance().openTransaction();
            Response<Boolean> output = new Response<>(market.openStore(userToken,storeName));
            this.logCommand(openStoreCommand.class, new openStoreCommand(this.uTokens, userToken, storeName));
            return output;
        }
        catch (IllegalArgumentException e){
            Logger.getInstance().logEvent("Service",String.format("Failed to open store, userToken:%s, storeName:%s, Error:%s" ,userToken,storeName, e.getMessage()));
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service - openStore", e.getMessage());
            return new Response<>(e,false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> writeProductReview(String userToken, String productName, String storeName, String reviewDescription, double points) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to write product review, userToken:%s, productName:%s, storeName:%s, reviewDescription:%s, points:%.2f" ,userToken, productName,storeName, reviewDescription, points));
        try
        {
            DAO.getInstance().openTransaction();
            market.writeProductReview(userToken, productName, storeName, reviewDescription, points);
            return new Response<>(true, null);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to write product review, userToken:%s, productName:%s, storeName:%s, reviewDescription:%s, points:%.2f, Error:%s" ,userToken, productName, storeName, reviewDescription, points, e.getMessage()));
            return new Response<>(e, true);
        }
        catch(Exception e)
        {
            Logger.getInstance().logBug("Service - writeProductReview", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> writeStoreReview(String userToken, String storeName, String reviewDescription, double points) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to write store review, userToken:%s, storeName:%s, reviewDescription:%s, points:%.2f" ,userToken, storeName, reviewDescription, points));
        try
        {
            DAO.getInstance().openTransaction();
            market.writeStoreReview(userToken, storeName, reviewDescription, points);
            return new Response<>(true, null);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to write store review, userToken:%s, storeName:%s, reviewDescription:%s, points:%.2f, Error:%s" ,userToken, storeName, reviewDescription, points, e.getMessage()));
            return new Response<>(e, true);
        }
        catch(Exception e)
        {
            Logger.getInstance().logBug("Service - writeStoreReview", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> sendQuestionsToStore(String userToken, String storeName, String message)
    {
        Logger.getInstance().logEvent("Service",String.format("Attempting to send questions to store, userToken:%s storeName:%s message:%s ",userToken,storeName, message));
        try
        {
            DAO.getInstance().openTransaction();
            market.sendQuestionsToStore(userToken, storeName, message);
            return new Response<>(true, null);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to send questions to store, userToken:%s, storeName:%s, message:%s, Error:%s" ,userToken, storeName, message, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e)
        {
            Logger.getInstance().logBug("Service - sendQuestionsToStore", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> sendComplaint(String userToken, String msg)
    {
        Logger.getInstance().logEvent("Service",String.format("Attempting to send complaint, userToken:%s msg:%s ",userToken,msg));
        try
        {
            DAO.getInstance().openTransaction();
            market.sendComplaint(userToken, msg);
            return new Response<>(true, null);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to send compliant, userToken:%s, message:%s, Error:%s" ,userToken, msg, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e)
        {
            Logger.getInstance().logBug("Service - sendComplaint", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<List<String>> getPurchaseHistory(String userToken, String userName) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get purchase history, userToken:%s userName:%s",userToken,userName));
        try
        {
            // todo: market should return a list of string and not object.. nothing to do with the object...
            List<ShoppingCartDTO> carts = market.getPurchaseHistory(userToken, userName);
            List<String> output = new LinkedList<>();
            for(ShoppingCartDTO cart:carts)
                output.add(cart.toString());
            return new Response<>(output);
        }
        catch (IllegalArgumentException e){
            Logger.getInstance().logEvent("Service",String.format("Failed to get purchase history, userToken:%s, userName:%s Error:%s" ,userToken,userName, e.getMessage()));
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
        Logger.getInstance().logEvent("Service",String.format("Attempting to add security question, userToken:%s question:%s answer:%s ",userToken,question, answer));
        try
        {
            DAO.getInstance().openTransaction();
            market.addSecurityQuestion(userToken, question, answer);
            return new Response<>(true, null);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to add security question, userToken:%s, question:%s, answer:%s, Error:%s" ,userToken, question, answer, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e)
        {
            Logger.getInstance().logBug("Service - addSecurityQuestions", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> addProductToStore(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Logger.getInstance().logEvent("Service", String.format("Add product to store invoked with parameters: token: %s productName:%s storeName:%s", userToken, productName, storeName));
        try {
            DAO.getInstance().openTransaction();
            boolean res = market.addProductToStore(userToken, productName, category, keyWords, description, storeName, quantity, price);
            this.logCommand(addProductToStoreCommand.class, new addProductToStoreCommand(this.uTokens, userToken, productName, category, keyWords, description, storeName, quantity, price));
            return new Response<>(res);
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to add product to store, userToken:%s, productName:%s, category:%s, description:%s, storeName:%s, quantity:%d, price:%.2f, Error:%s" ,userToken, productName, category, description, storeName, quantity, price, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - addProductToStore", "Bug in addProductToStore!");
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> removeProductFromStore(String userToken, String productName,String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Delete product from store invoked with parameters: token: %s productName:%s storeName:%s", userToken, productName, storeName));
        try{
            DAO.getInstance().openTransaction();
            return new Response<>(market.removeProductFromStore(userToken,productName,storeName));
        }
        catch (IllegalArgumentException e){
            Logger.getInstance().logEvent("Service",String.format("Failed to remove product from store, userToken:%s, productName:%s, storeName:%s, Error:%s" ,userToken,productName, storeName, e.getMessage()));
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service - removeProductFromStore", "Bug in remove product");
            return new Response<>(e,false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> updateProduct(String userToken, String oldProductName,String newProductName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Logger.getInstance().logEvent("Service", String.format("Update product to store invoked with parameters: token: %s productName:%s storeName:%s", userToken, oldProductName, storeName));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.updateProductInStore(userToken, oldProductName,newProductName, category, keyWords, description, storeName, quantity, price));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to update product, userToken:%s, oldProductName:%s, newProductName:%s, category:%s, description:%s, storeName:%s, quantity:%d, price:%.2f, Error:%s" ,userToken, oldProductName, newProductName, category, description, storeName, quantity, price, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - updateProduct", "Bug in update product");
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateSimpleDiscount(String userToken, String store, LocalDate until, Double percent) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateSimpleDiscount(userToken, store, until, percent));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateSimpleDiscount", "Bug in update product");
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateSecretDiscount(String userToken, String store, LocalDate until, Double percent, String secretCode) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateSecretDiscount(userToken, store, until, percent, secretCode));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateSecretDiscount", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateConditionalDiscount(String userToken, String store, LocalDate until, Double percent, int condID) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateConditionalDiscount(userToken, store, until, percent, condID));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateConditionalDiscount", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateMaximumCompositeDiscount(String userToken, String store, LocalDate until, List<Integer> discounts) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateMaximumCompositeDiscount(userToken, store, until, discounts));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateMaximumCompositeDiscount", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreatePlusCompositeDiscount(String userToken, String store, LocalDate until, List<Integer> discounts) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreatePlusCompositeDiscount(userToken, store, until, discounts));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreatePlusCompositeDiscount", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> SetDiscountToProduct(String userToken, String store, int discountID, String productName) {
        try {
            DAO.getInstance().openTransaction();
            market.SetDiscountToProduct(userToken, store, discountID, productName);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - SetDiscountToProduct", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> SetDiscountToStore(String userToken, String store, int discountID) {
        try {
            DAO.getInstance().openTransaction();
            market.SetDiscountToStore(userToken, store, discountID);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - SetDiscountToStore",  e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateBasketValueCondition(String userToken, String store, double requiredValue) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateBasketValueCondition(userToken, store, requiredValue));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateBasketValueCondition",  e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateCategoryAmountCondition(String userToken, String store, String category, int amount) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateCategoryAmountCondition(userToken, store, category, amount));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateCategoryAmountCondition",  e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateProductAmountCondition(String userToken, String store, String productName, int amount) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateProductAmountCondition(userToken, store, productName, amount));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateProductAmountCondition", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateLogicalAndCondition(String userToken, String store, List<Integer> conditionIds) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateLogicalAndCondition(userToken, store, conditionIds));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateLogicalAndCondition",  e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateLogicalOrCondition(String userToken, String store, List<Integer> conditionIds) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateLogicalOrCondition(userToken, store, conditionIds));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateLogicalOrCondition",  e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Integer> CreateLogicalXorCondition(String userToken, String store, int id1, int id2) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.CreateLogicalXorCondition(userToken, store, id1, id2));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - CreateLogicalXorCondition",  e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> SetConditionToDiscount(String userToken, String store, int discountId, int ConditionID) {
        try {
            DAO.getInstance().openTransaction();
            market.SetConditionToDiscount(userToken, store, discountId, ConditionID);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - SetConditionToDiscount",  e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> SetConditionToStore(String userToken, String store, int ConditionID) {
        try {
            DAO.getInstance().openTransaction();
            market.SetConditionToStore(userToken, store, ConditionID);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - SetConditionToStore", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> addDiscountPasswordToBasket(String userToken, String storeName, String Password) {
        try {
            DAO.getInstance().openTransaction();
            market.addDiscountPasswordToBasket(userToken, storeName, Password);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - addDiscountPasswordToBasket", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> addNormalPolicy(String userToken, String storeName, String productName, Double price) {
        try {
            DAO.getInstance().openTransaction();
            market.addNormalPolicy(userToken, storeName, productName, price);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service", "Bug in adding Policy.");
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> addBargainPolicy(String userToken, String StoreName, String productName, Double originalPrice) {
        try {
            DAO.getInstance().openTransaction();
            market.addBargainPolicy(userToken, StoreName, productName, originalPrice);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service", "Bug in adding Policy.");
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> bidOnProduct(String userToken,  String storeName, String productName, Double costumePrice, PaymentInformation paymentInformation, SupplyingInformation supplyingInformation) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.bidOnProduct(userToken, storeName, productName, costumePrice, paymentInformation, supplyingInformation));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service", "Bug in biding.");
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<List<BidDTO>> getUserBids(String userToken, String storeName, String productName) {
        try {
            return new Response<>(market.getUserBids(userToken, storeName,  productName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service", "Bug in biding.");
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> ApproveBid(String userToken, String storeName, String productName, String username) {
        try {
            DAO.getInstance().openTransaction();
            market.ApproveBid( userToken, storeName, productName, username);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service", "Bug in biding.");
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> DeclineBid(String userToken, String storeName, String productName, String username) {
        try {
            DAO.getInstance().openTransaction();
            market.DeclineBid( userToken, storeName, productName, username);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service", "Bug in biding.");
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> CounterOfferBid(String userToken, String storeName, String productName, String username, Double offer) {
        try {
            market.CounterOfferBid( userToken, storeName, productName, username, offer);
            return new Response<>(true);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service", "Bug in biding.");
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> appointStoreOwner(String userToken, String userToAppoint, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to appoint store owner with parameters: token: %s userToAppoint: %s storeName:%s", userToken, userToAppoint, storeName));
        try {
            DAO.getInstance().openTransaction();
            Response<Boolean> output = new Response<>(market.appointStoreOwner(userToken, userToAppoint, storeName));
            this.logCommand(appointStoreOwnerCommand.class, new appointStoreOwnerCommand(this.uTokens, userToken, userToAppoint, storeName));
            return output;
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to appoint store owner, userToken:%s, userToAppoint:%s, storeName:%s, Error:%s" ,userToken, userToAppoint,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - appointStoreOwner", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> removeStoreOwnerAppointment(String userToken, String userAppointed, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove a store owner appointment parameters: token: %s userAppointed: %s storeName:%s", userToken, userAppointed, storeName));
        try {
            DAO.getInstance().openTransaction();
            Response<Boolean> output = new Response<>(market.removeStoreOwnerAppointment(userToken, userAppointed, storeName));
            this.logCommand(removeStoreOwnerAppointmentCommand.class, new removeStoreOwnerAppointmentCommand(this.uTokens, userToken, userAppointed, storeName));
            return output;
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to remove store owner appointment, userToken:%s, userAppointed:%s, storeName:%s, Error:%s" ,userToken, userAppointed,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - removeStoreOwnerAppointment", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> appointStoreManager(String userToken, String userToAppoint, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to appoint store manager with parameters: token: %s userToAppoint: %s storeName:%s", userToken, userToAppoint, storeName));
        try {
            DAO.getInstance().openTransaction();
            Response<Boolean> r = new Response<>(market.appointStoreManager(userToken, userToAppoint, storeName));
            logCommand(appointStoreManagerCommand.class, new appointStoreManagerCommand(this.uTokens, userToken, userToAppoint, storeName));
            return r;
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to appoint store manager, userToken:%s, userToAppoint:%s, storeName:%s, Error:%s" ,userToken, userToAppoint,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - appointStoreManager", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> removeStoreManagerAppointment(String userToken, String userAppointed, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove a store manager:%s from store:%s", userAppointed, storeName));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.removeStoreManager(userToken, userAppointed, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to remove store manager appointment, userToken:%s, userAppointed:%s, storeName:%s, Error:%s" ,userToken, userAppointed,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - removeStoreManagerAppointment", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> allowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant update products permission to:%s in store:%s", managerName, storeName));
        try {
            DAO.getInstance().openTransaction();
            Response<Boolean> output = new Response<>(market.allowManagerUpdateProducts(userToken, managerName, storeName));
            this.logCommand(AllowManagerUpdateProductsCommand.class ,new AllowManagerUpdateProductsCommand(this.uTokens, userToken, managerName, storeName));
            return output;
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to allow manager update products, userToken:%s, managerName:%s, storeName:%s, Error:%s" ,userToken, managerName,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - allowManagerUpdateProducts", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> disAllowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow update products permission to:%s in store:%s", managerName, storeName));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.disallowManagerUpdateProducts(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to disallow manager update products, userToken:%s, managerName:%s, storeName:%s, Error:%s" ,userToken, managerName,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - disAllowManagerUpdateProducts", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> allowManagerGetHistory(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant view purchase history permission to:%s in store:%s", managerName, storeName));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.allowManagerViewPurchaseHistory(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to allow manager to get history, userToken:%s, managerName:%s, storeName:%s, Error:%s" ,userToken, managerName,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - allowManagerGetHistory", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> disAllowManagerGetHistory(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow viewing purchase history permission to:%s in store:%s", managerName, storeName));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.disallowManagerViewPurchaseHistory(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to disallow manager to get history, userToken:%s, managerName:%s, storeName:%s, Error:%s" ,userToken, managerName,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - disAllowManagerGetHistory", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> allowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to grant answer and take requests permission to:%s in store:%s", managerName, storeName));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.allowManagerAnswerAndTakeRequests(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to allow manager answer and take requests, userToken:%s, managerName:%s, storeName:%s, Error:%s" ,userToken, managerName,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - allowManagerAnswerAndTakeRequests", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> disAllowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to disallow answer and take requests permission to:%s in store:%s", managerName, storeName));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.disallowManagerAnswerAndTakeRequests(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to disallow manager answer and take requests, userToken:%s, managerName:%s, storeName:%s, Error:%s" ,userToken, managerName,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - disAllowManagerAnswerAndTakeRequests", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> allowManagerBargainPreducts(String userToken, String managerName, String storeName) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.allowManagerBargainProducts(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - allowManagerBargainPreducts", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> disallowManagerBargainProducts(String userToken, String managerName, String storeName) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.disallowManagerBargainProducts(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - disallowManagerBargainProducts", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> allowManagerPolicyProducts(String userToken, String managerName, String storeName) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.allowManagerPolicyProducts(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - allowManagerPolicyProducts", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> disallowManagerPolicyProducts(String userToken, String managerName, String storeName) {
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.disallowManagerPolicyProducts(userToken, managerName, storeName));
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - disallowManagerPolicyProducts", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> closeStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to close store:%s", storeName));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.closeStore(userToken, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to close store, userToken:%s, storeName:%s, Error:%s" ,userToken,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - closeStore", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> reopenStore(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to reopen store:%s", storeName));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.reopenStore(userToken, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to reopen store, userToken:%s, storeName:%s, Error:%s" ,userToken,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - reopenStore", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<List<String>> getStoreStaff(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to view store staff in store:%s", storeName));
        try {
            HashMap<User, String> map = market.getStoreStaff(userToken, storeName);
            List<String> toReturn=new LinkedList<>();
            for (User u : map.keySet())
                toReturn.add((u.getUserName() + ": " + map.get(u)));
            return new Response<>(toReturn);
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to get store stuff, userToken:%s, storeName:%s, Error:%s" ,userToken,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - getStoreStaff", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<String>> receiveQuestionsFromBuyers(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to receive questions from buyers from store:%s", storeName));
        try {
            return new Response<>(market.receiveQuestionsFromBuyers(userToken, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to receive questions from buyers, userToken:%s, storeName:%s, Error:%s" ,userToken,storeName, e.getMessage()));
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
            DAO.getInstance().openTransaction();
            return new Response<>(market.sendRespondToBuyer(userToken, storeName, userToRespond, msg));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to send response to buyers, userToken:%s, storeName:%s, userToRespond:%s, msg:%s, Error:%s" ,userToken,storeName,userToRespond, msg, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - sendRespondToBuyers", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<List<String>> getStorePurchaseHistory(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get store%s purchase history", storeName));
        try {
            Map<ShoppingBasketDTO, LocalDateTime> baskets = market.getStorePurchaseHistory(userToken, storeName);
            List<String> output = new LinkedList<>();
            for (ShoppingBasketDTO basket : baskets.keySet()) {
                HashMap<ProductDTO, Integer> products = new HashMap<>();
                for (ProductDTO product : basket.getProductsQuantity().keySet())
                    products.put(product, basket.getProductsQuantity().get(product));
                output.add(new PurchaseDTO(products, baskets.get(basket)).toString());
            }
            return new Response<>(output);
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to get store purchase history, userToken:%s, storeName:%s, Error:%s" ,userToken,storeName, e.getMessage()));
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
            DAO.getInstance().openTransaction();
            return new Response<>(market.deleteStore(userToken, storeName));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to delete store, userToken:%s, storeName:%s, Error:%s" ,userToken,storeName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - deleteStore", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<Boolean> deleteUser(String userToken, String userName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to remove user %s", userName));
        try {
            DAO.getInstance().openTransaction();
            Response<Boolean> output = new Response<>(market.deleteUser(userToken, userName));
            this.logCommand(deleteUserCommand.class, new deleteUserCommand(this.uTokens, userToken, userName));
            return output;
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to delete user, userToken:%s, userName:%s, Error:%s" ,userToken,userName, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - deleteUser", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<List<String>> receiveMessages(String userToken) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get all messages for token: %s", userToken));
        try {
            return new Response<>(market.receiveMessages(userToken));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to receive messages, userToken:%s, Error:%s" ,userToken, e.getMessage()));
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
            DAO.getInstance().openTransaction();
            return new Response<>(market.respondToMessage(userToken, userToRespond, msg));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to respond to message, userToken:%s, userToRespond:%s, msg:%s, Error:%s" ,userToken,userToRespond, msg, e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - respondToMessage", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<String> getNumberOfLoggedInUsersPerDate(String userToken, LocalDate date) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get system stats: logged in users per date: %s", date.toString()));
        try {
            return new Response<>(market.getNumberOfLoggedInUsersPerDate(userToken, date));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to get number of logged in users per date, userToken:%s, date:%s, Error:%s" ,userToken,date.toString(), e.getMessage()));
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
            Logger.getInstance().logEvent("Service",String.format("Failed to get number of purchases per date, userToken:%s, date:%s, Error:%s" ,userToken,date.toString(), e.getMessage()));
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
            Logger.getInstance().logEvent("Service",String.format("Failed to get number of registered users per date, userToken:%s, date:%s, Error:%s" ,userToken,date.toString(), e.getMessage()));
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - getNumberOfRegisteredUsersPerDate", e.getMessage());
            return new Response<>(e, false);
        }
    }

    public Response<Boolean> changePassword(String userToken, String oldPassword, String newPassword)
    {
        Logger.getInstance().logEvent("Service", String.format("Attempting to change password: userToken: %s", userToken));
        try
        {
            DAO.getInstance().openTransaction();
            market.changePassword(userToken, oldPassword, newPassword);
            return new Response<>(true, null);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to change password, userToken:%s" ,userToken, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e)
        {
            Logger.getInstance().logBug("Service - changePassword", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    public Response<Boolean> changeUsername(String userToken, String newUsername)
    {
        Logger.getInstance().logEvent("Service", String.format("Attempting to change username: userToken: %s, newUserName:%s", userToken, newUsername));
        try
        {
            DAO.getInstance().openTransaction();
            market.changeUsername(userToken, newUsername);
            return new Response<>(true, null);
        }
        catch (IllegalArgumentException e) {
            Logger.getInstance().logEvent("Service",String.format("Failed to change username, userToken:%s, newUserName:%s" ,userToken,newUsername, e.getMessage()));
            return new Response<>(e, true);
        }
        catch (Exception e)
        {
            Logger.getInstance().logBug("Service - changeUsername", e.getMessage());
            return new Response<>(e, false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    public Response<Boolean> isMemberLoggedOut(String userToken)
    {
        Logger.getInstance().logEvent("Service", String.format("Checking if member is logged out: userToken: %s", userToken));
        try
        {
            boolean res = market.isMemberLoggedOut(userToken);
            return new Response<>(res, null);
        }
        catch (Exception e)
        {
            Logger.getInstance().logBug("Service - isMemberLoggedOut", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<List<StoreDTO>> getAllStoresOfUser(String userToken) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to get all stores of user %s", userToken));
        try {
            List<StoreDTO> storeList = market.getAllStoresOf(userToken);
//            LinkedList<StoreDTO> storeList = new LinkedList<>();
            return new Response<>(storeList);
        } catch (IllegalArgumentException e) {
            return new Response<>(e, true);
        } catch (Exception e) {
            Logger.getInstance().logBug("Service - getAllStoresOfUser", e.getMessage());
            return new Response<>(e, false);
        }
    }

    @Override
    public Response<Boolean> assignWStoUserToken(String userToken, WsContext ctx) {
        Logger.getInstance().logEvent("Service","Assigning a websocket to a user");
        try{
            return new Response<>(market.assignWStoUserToken(userToken,ctx));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);

        }
        catch (Exception e){
            Logger.getInstance().logBug("Service->assignWSToUserToken",e.getMessage());
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> leaveWSforUserToken(String userToken) {
        Logger.getInstance().logEvent("Service","Leaving websocket");
        try{
            return new Response<>(market.leaveWSforUserToken(userToken));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);

        }
        catch (Exception e){
            Logger.getInstance().logBug("Service->leaveWSforUserToken",e.getMessage());
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<String> getLoggedInVSRegistered(String userToken) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get logged in vs registered stats by token:%s",userToken));
        try {
            return new Response<>(market.getLoggedInVSRegistered(userToken));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);

        }
        catch (Exception e){
            Logger.getInstance().logBug("Service->getLoggedInVSRegistered",e.getMessage());
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> verifyAdminDetails(String username, String password) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to boot system"));
        try{
            return new Response<>(market.verifyAdmin(username,password));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);
        }
        catch (Exception e){
            Logger.getInstance().logBug("Service->verifyAdminDetails",e.getMessage());
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<Boolean> assignWStoStats(String userToken) {
        Logger.getInstance().logEvent("Service","Assigning a websocket to stats");
        try{
            return new Response<>(market.assignWStoStats(userToken));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);

        }
        catch (Exception e){
            Logger.getInstance().logBug("Service->assignWSToStats",e.getMessage());
            return new Response<>(e,false);
        }
    }

    @Override
    public Response<String> getStatsPerDate(String userToken, LocalDate date) {
        Logger.getInstance().logEvent("Service",String.format("Attempting to get system stats with token:%s",userToken));
        try {
            return new Response<>(market.getStatsByDate(userToken,date));
        }
        catch (IllegalArgumentException e){
            return new Response<>(e,true);

        }
        catch (Exception e){
            Logger.getInstance().logBug("Service->getStatsPerDate",e.getMessage());
            return new Response<>(e,false);
        }
    }


    @Override
    public Response<String> approveOwnerAppointment(String userToken, String userNameToApprove, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to approve %s owner appointment with userToken: %s", userNameToApprove,userToken));
        try {
            DAO.getInstance().openTransaction();
            return new Response<> (market.approveOwnerAppointment(userToken,userNameToApprove,storeName));
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e,true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service->approveOwnerAppointment",e.getMessage());
            return new Response<>(e,false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<String> declineOwnerAppointment(String userToken, String userNameToDecline, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to decline %s owner appointment with userToken: %s", userNameToDecline,userToken));
        try {
            DAO.getInstance().openTransaction();
            return new Response<>(market.declineOwnerAppointment(userToken,userNameToDecline,storeName));
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e,true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service->declineOwnerAppointment",e.getMessage());
            return new Response<>(e,false);
        }
        finally {
            DAO.getInstance().commitTransaction();
        }
    }

    @Override
    public Response<List<OwnerAppointmentRequestDTO>> getOwnerAppointmentRequests(String userToken, String storeName) {
        Logger.getInstance().logEvent("Service", String.format("Attempting to fetch %s owner appointment requests, request made by user token : %s", storeName,userToken));
        try {
            return new Response<>(market.getOwnerAppointmentRequests(userToken,storeName));
        }
        catch (IllegalArgumentException e) {
            return new Response<>(e,true);
        }
        catch (Exception e) {
            Logger.getInstance().logBug("Service->getOwnerAppointmentRequests",e.getMessage());
            return  new Response<>(e,false);
        }
    }
}
