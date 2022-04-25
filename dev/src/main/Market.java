package main;

import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Stores.ProductReview;
import main.Stores.StoreReview;

import main.DTO.ShoppingCartDTO;
import main.Logger.Logger;
import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.Security.ISecurity;
import main.Security.Security;
import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;
import main.Stores.IStore;
import main.Stores.Product;
import main.Users.StorePermission;
import main.Users.User;

import main.utils.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Market {

    /**
     * usersByName is a hashmap that maps username -> User
     * each username is unique in the system
     * <p>
     * connectedUsers is a hashmap that maps token -> connected user
     * once a user disconnects he's removed from this hashmap
     * the token is generated by the system, randomly
     */
    private ConcurrentHashMap<String, User> usersByName; //key=username
    private ConcurrentHashMap<String, User> connectedUsers; //key=userToken, generated randomly by system
    private ConcurrentHashMap<String, IStore> stores; //key=store name
    private ISecurity security_controller;
    private AtomicInteger guestCounter;
    private ISupplying supplyingSystem;
    private IPayment paymentSystem;

    private NotificationBus bus;

    private ConcurrentHashMap <LocalDate, SystemStats> systemStatsByDate;
    private enum StatsType{Register, Login, Purchase}

    public Market(){
        usersByName=new ConcurrentHashMap<>();
        connectedUsers=new ConcurrentHashMap<>();
        stores=new ConcurrentHashMap<>();
        guestCounter=new AtomicInteger(1);
        bus =new NotificationBus();
        systemStatsByDate=new ConcurrentHashMap<>();
        security_controller = new Security();
        supplyingSystem = new SupplyingAdapter();
        paymentSystem = new PaymentAdapter();
    }

    /***
     * This function should be called on every system start up.
     * @return new unique user token.
     */
    public String ConnectGuest() {
        String new_token = generateToken();
        User new_guest = new User(new_token);
        connectedUsers.put(new_token, new_guest);
        Logger.getInstance().logEvent("Market", String.format("New guest connected %s.", new_guest.getUserName()));
        return new_token;
    }

    public User DisconnectGuest(String user_token) {
        if (!connectedUsers.containsKey(user_token)) {
            Logger.getInstance().logBug("Market", String.format("invalid user token attempted to disconnect %s", user_token));
            throw new IllegalArgumentException("user token isn't connected.");
        }
        if (connectedUsers.get(user_token).getIsLoggedIn()) {
            throw new IllegalArgumentException("user token is a a member not a guest.");
        }
        User leaving_user = connectedUsers.remove(user_token);
        Logger.getInstance().logEvent("Market", String.format("User %s left the system.", leaving_user.getUserName()));
        return leaving_user;
    }

    private void addStats(StatsType type)
    {
        LocalDate date = LocalDate.now();
        if(this.systemStatsByDate.containsKey(date))
        {
            SystemStats systemStats = this.systemStatsByDate.get(date);
            switch (type) {
                case Register -> systemStats.addRegister();
                case Login -> systemStats.addLogIn();
                case Purchase -> systemStats.addPurchase();
            }
        }
        else
        {
            SystemStats newSystemStats = new SystemStats(date);
            switch (type) {
                case Register -> newSystemStats.addRegister();
                case Login -> newSystemStats.addLogIn();
                case Purchase -> newSystemStats.addPurchase();
            }
            this.systemStatsByDate.put(date, newSystemStats);
        }
    }

    public boolean Register(String userName, String password) {
        if (usersByName.containsKey(userName)) {
            throw new IllegalArgumentException("username is taken.");
        }
        if (!isValidPass(password, userName)) {
            throw new IllegalArgumentException("password is not secure enough.");
        }
        User new_user = new User(false, userName, security_controller.hashPassword(password));
        bus.register(new_user);

        usersByName.put(userName, new_user);
        Logger.getInstance().logEvent("Market", String.format("New user registered with username: %s", userName));
        addStats(StatsType.Register);
        return true;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public User Login(String token, String userName, String password) {
        if (!usersByName.containsKey(userName)) {
            throw new IllegalArgumentException("username doesn't exist.");
        }
        if (!connectedUsers.containsKey(token)) {
            Logger.getInstance().logBug("Market", String.format("token %s isn't in the system and attempted to log in.", token));
            throw new IllegalArgumentException("token isn't connected in the system.");
        }
        if (connectedUsers.get(token).getIsLoggedIn()) {
            throw new IllegalArgumentException("user already logged in.");
        }
        User u = usersByName.get(userName);
        if (!u.getHashed_password().equals(security_controller.hashPassword(password))) {
            throw new IllegalArgumentException("Incorrect password.");
        }
        Logger.getInstance().logEvent("Market", String.format("%s logged in.", userName));
        connectedUsers.put(token, u);
        u.LogIn();
        addStats(StatsType.Login);
        return u;
    }



    public IStore getStoreByName(String name) {
        return this.stores.get(name);
    }

    public List<String> getStoresByString(String name) {
        List<String> res = new LinkedList<>();
        for (String key : this.stores.keySet()) {
            if (stringFunctions.calculate_distance(name, key) <= 3)
                res.add(key);
        }
        return res;
    }

    public List<Product> getStoreProducts(String storeName) {
        List<Product> res = new LinkedList<>();
        IStore st = this.getStoreByName(storeName);
        if (st == null)
            throw new IllegalArgumentException("store doesn't exist.");
        for (String productName : st.getProductsByName().keySet())
            res.add(st.getProductsByName().get(productName));
        return res;
    }

    public List<Product> getProductsByAttributes(String productName, String category, String keyWord, Double productRating, Double storeRating, Double minPrice, Double maxPrice){
        List<Product> result = new LinkedList<>();
        for (IStore currStr : this.stores.values())
            for (Product currPrd : currStr.getProductsByName().values()) {
                if (productName == null || currPrd.getName().equals(productName))
                    if (category == null || currPrd.getCategory().equals(category))
                        if (keyWord == null || currPrd.hasKeyWord(keyWord))
                            if (productRating == null) //TODO: || rating = productRating
                                if (storeRating == null) //TODO: || rating = productRating
                                    if (minPrice == null || maxPrice == null || (currPrd.getPrice() <= maxPrice && currPrd.getPrice() >= minPrice))
                                        result.add(currPrd);
            }
        return result;
    }

    public boolean addProductToCart(String userToken, String storeName, String productName, int quantity) {
        User us = this.connectedUsers.get(userToken);
        if (quantity <= 0)
            throw new IllegalArgumentException("quantity is lesss than or equal to 0.");
        if (us == null) {
            Logger.getInstance().logBug("Market", String.format("Unknown user token, %s.", userToken));
            throw new IllegalArgumentException("Unkown user token.");
        }
        IStore st = this.getStoreByName(storeName);
        if(st == null) {
            throw new IllegalArgumentException("Store doesn't exist.");
        }
        return us.addProductToCart(st, productName, quantity);
    }

    public boolean RemoveProductFromCart(String userToken, String storeName, String productName, int quantity) {
        User us = this.connectedUsers.get(userToken);
        if (quantity <= 0)
            throw new IllegalArgumentException("quantity is lesss than or equal to 0.");
        if (us == null) {
            Logger.getInstance().logBug("Market", String.format("Unknown user token, %s.", userToken));
            throw new IllegalArgumentException("Unkown user token.");
        }
        IStore st = this.getStoreByName(storeName);
        if(st == null) {
            throw new IllegalArgumentException("Store doesn't exist.");
        }
        return us.RemoveProductFromCart(st, productName, quantity);
    }

    public ShoppingCart getUserCart(String userToken) {
        User us = this.connectedUsers.get(userToken);
        if (us == null) {
            Logger.getInstance().logBug("Market", String.format("Unknown user token, %s.", userToken));
            throw new IllegalArgumentException("Unkown user token.");
        }
        return us.getCart();
    }


    public boolean addProductToStore(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Pair<User, IStore> p=getConnectedUserAndStore(userToken,storeName);
        return p.first.addProductToStore(p.second,productName,category,keyWords,description,quantity,price);
    }

    public boolean updateProductInStore(String userToken, String oldProductName,String newProductName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.updateProductToStore(p.second, oldProductName,newProductName, category, keyWords, description, quantity, price);
    }

    public boolean appointStoreOwner(String userToken, String userToAppoint, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        User user_to_appoint = usersByName.get(userToAppoint);
        if (user_to_appoint == null)
            throw new IllegalArgumentException("The user to appoint doesn't exist!");

        return p.first.appointOwnerToStore(p.second, user_to_appoint);
    }

    public boolean removeStoreOwnerAppointment(String userToken, String userAppointed, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        User appointed_user = usersByName.get(userAppointed);
        if (appointed_user == null)
            throw new IllegalArgumentException("The user appointed doesn't exist in the system");

        return p.first.removeOwnerAppointment(p.second, appointed_user);
    }

    private Pair<User, IStore> getConnectedUserAndStore(String userToken, String storeName) {
        User user = connectedUsers.get(userToken);
        if (user == null)
            throw new IllegalArgumentException("User not logged in");

        IStore IStore = stores.get(storeName);
        if (IStore == null)
            throw new IllegalArgumentException("No such store");

        return new Pair<>(user, IStore);
    }

    public boolean appointStoreManager(String userToken, String userToAppoint, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        User user_to_appoint = usersByName.get(userToAppoint);
        if (user_to_appoint == null)
            throw new IllegalArgumentException("The user to appoint doesn't exist!");
        if (p.first == user_to_appoint)
            throw new IllegalArgumentException("Can't appoint yourself!");

        return p.first.appointManagerToStore(p.second, user_to_appoint);
    }

    private boolean allowOrDisallowPermission(String userToken, String managerName, String storeName, StorePermission permission, boolean shouldGrant) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        User manager = usersByName.get(managerName);
        if (manager == null)
            throw new IllegalArgumentException("There's no such manager");

        return p.first.grantOrDeletePermission(manager, p.second, shouldGrant, permission);
    }

    public boolean allowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.UpdateAddProducts, true);
    }

    public boolean disallowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.UpdateAddProducts, false);
    }

    public boolean allowManagerViewPurchaseHistory(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.ViewStoreHistory, true);
    }

    public boolean disallowManagerViewPurchaseHistory(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.ViewStoreHistory, false);
    }

    public boolean allowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.AnswerAndTakeRequests, true);
    }

    public boolean disallowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.AnswerAndTakeRequests, false);
    }

    public boolean removeStoreManager(String userToken, String userAppointed, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        User manager = usersByName.get(userAppointed);
        if (manager == null)
            throw new IllegalArgumentException("The user doesn't exist in the system!");

        return p.first.removeManagerAppointment(p.second, manager);
    }

    public boolean closeStore(String userToken, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.closeStore(p.second, bus);
    }

    public boolean reopenStore(String userToken, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.reOpenStore(p.second, bus);
    }

    public HashMap<User, String> getStoreStaff(String userToken, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.getStoreStaff(p.second);
    }

    public List<Pair<String, String>> receiveQuestionsFromBuyers(String userToken, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.receiveQuestionsFromStore(p.second,bus);
    }

    public boolean sendRespondToBuyer(String userToken, String storeName, String userToRespond, String msg) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        User toRespond = usersByName.get(userToRespond);
        if (toRespond == null)
            throw new IllegalArgumentException("No such user to respond to");
        return p.first.sendRespondFromStore(p.second, toRespond, msg, bus);
    }

    public ConcurrentHashMap<ShoppingBasket, LocalDateTime> getStorePurchaseHistory(String userToken, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.getStorePurchaseHistory(p.second);
    }

    public boolean deleteStore(String userToken, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        if (p.first.removeStore(p.second)) {
            stores.remove(storeName);
            return true;
        }
        return false;
    }

    public boolean deleteUser(String userToken, String userName) {

        User admin = connectedUsers.get(userToken);
        if (admin == null)
            throw new IllegalArgumentException("No such admin in the system");
        User toDelete = usersByName.get(userName);
        if (admin.deleteUser(toDelete)) {
            usersByName.remove(toDelete.getUserName());
            return true;
        }
        return false;
    }

    public List<String> receiveMessages(String userToken) {
        User user = connectedUsers.get(userToken);
        if (user == null)
            throw new IllegalArgumentException("User isn't connected");
        return bus.getMessagesFromUserRequest(user);
    }

    public boolean respondToMessage(String userToken, String userToRespond, String msg) {
        User responding_user = connectedUsers.get(userToken);
        if(!responding_user.isAdmin())
            throw new IllegalArgumentException("Only admin can respond to messages");
        User user_receiving_msg = usersByName.get(userToRespond);
        if(user_receiving_msg==null)
            throw new IllegalArgumentException("No such user to respond to");

        bus.addMessage(user_receiving_msg, String.format("From user:%s \n Message content: %s", responding_user.getUserName(), msg));
        return true;
    }

    public String getNumberOfLoggedInUsersPerDate(String userToken, LocalDate date) {
        return String.valueOf(getStats(userToken, date).getNumOfLoggedIn());
    }

    private SystemStats getStats(String userToken, LocalDate date) {
        User admin = connectedUsers.get(userToken);
        if (!admin.isAdmin())
            throw new IllegalArgumentException("This isn't a system admin");

        if (systemStatsByDate.get(date) == null)
            throw new IllegalArgumentException("No stats for the specific date");
        return systemStatsByDate.get(date);
    }

    public String getNumberOfPurchasesPerDate(String userToken, LocalDate date) {
        return String.valueOf(getStats(userToken, date).getNumOfPurchases());
    }

    public String getNumberOfRegisteredUsersPerDate(String userToken, LocalDate date) {
        return String.valueOf(getStats(userToken, date).getNumOfRegistered());
    }

    /**
     * Create Default system manager
     */
    public void initialize() {
        String adminUserName = "admin";
        String adminHashPassword = security_controller.hashPassword("admin");
        User admin = new User(true, adminUserName, adminHashPassword);
        usersByName.put("admin", admin);
        bus.register(admin);
    }


    public boolean openStore(String userToken, String storeName) throws Exception{
        User founder = connectedUsers.get(userToken);
        synchronized (stores) {
            if(founder== null)
            {
                throw new Exception("Invalid user token");
            }
            if (!usersByName.containsKey(founder.getUserName()))
                throw new IllegalArgumentException("This user isn't registered to the system!");
            if (stores.containsKey(storeName))
                throw new IllegalArgumentException("There's already a store with that name in the system");
        IStore newIStore =founder.openStore(storeName);
        stores.put(storeName, newIStore);
        bus.register(newIStore);
        }
        return true;
    }

    public boolean removeProductFromStore(String userToken, String productName, String storeName) {
        Pair<User, IStore> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.removeProductFromStore(productName,p.second);
    }

    public void addSecurityQuestion(String userToken, String question, String answer) throws Exception
    {
        if(!connectedUsers.containsKey(userToken))
        {
            throw new Exception("Invalid user token");
        }
        User u = connectedUsers.get(userToken);
        if(!usersByName.containsKey(u.getUserName()))
        {
            throw new Exception("User is a not a member");
        }
        u.addSecurityQuestion(question, answer);
    }

    public void logout(String token) throws Exception
    {
        if(!connectedUsers.containsKey(token))
        {
            throw new Exception("User is not logged in");
        }
        User u = connectedUsers.get(token);
        String userName = u.getUserName();
        if(!usersByName.containsKey(userName))
        {
            throw new Exception("User is not a member");
        }
        if(!u.getIsLoggedIn())
        {
            throw new Exception("Member is not logged in");
        }
        u.logout();
    }

    public void purchaseCart(String userToken, PaymentInformation pinfo, SupplyingInformation sinfo) throws Exception
    {
        //User purchase history update
        if(!connectedUsers.containsKey(userToken))
        {
            throw new Exception("Invalid user token");
        }
        User u = connectedUsers.get(userToken);
        u.purchaseCart(bus, pinfo, sinfo);
        addStats(StatsType.Purchase);
    }

    public List<ShoppingCartDTO> getPurchaseHistory(String userToken) throws Exception{
        if(!connectedUsers.containsKey(userToken))
        {
            throw new Exception("Invalid user token");
        }
        User u = connectedUsers.get(userToken);
        if(!u.getIsLoggedIn())
        {
            throw new Exception("User is not logged in");
        }
        List<ShoppingCart> purchaseHistory = u.getPurchaseHistory();
        List<ShoppingCartDTO> scDTO = new LinkedList<>();
        for(ShoppingCart sc : purchaseHistory)
        {
            scDTO.add(new ShoppingCartDTO(sc));
        }
        return scDTO;
    }

    public void writeProductReview(String userToken, String productName, String storeName, String reviewDescription, double points) throws Exception{
        if(!connectedUsers.containsKey(userToken))
        {
            throw new Exception("Invalid user token");
        }
        User u = connectedUsers.get(userToken);
        Product prod = u.findProductInHistoryByNameAndStore(productName, storeName);
        if(prod == null)
            throw new Exception("Product was not found in user's purchase history");
        ProductReview pReview = new ProductReview(u, prod, reviewDescription, points);
        prod.addReview(pReview);
    }

    public void writeStoreReview(String userToken, String storeName, String reviewDescription, double points) throws Exception{
        if(!connectedUsers.containsKey(userToken))
        {
            throw new Exception("Invalid user token");
        }
        User u = connectedUsers.get(userToken);
        IStore store = u.getStoreInPurchaseHistory(storeName);
        if(store==null)
        {
            throw new Exception("Product was not found in user's purchase history");
        }
        StoreReview sReview = new StoreReview(u, store, reviewDescription, points);
        store.addReview(sReview);
    }

    public void changePassword(String userToken, String oldPassword, String newPassword)throws Exception {
        if(!connectedUsers.containsKey(userToken))
        {
            throw new Exception("Invalid user token");
        }
        User u = connectedUsers.get(userToken);
        if(!isValidPass(newPassword, u.getUserName()))
        {
            throw new Exception("Invalid password");
        }
        String oldPassHashed = this.security_controller.hashPassword(oldPassword);
        if(!oldPassHashed.equals(u.getHashed_password()))
        {
            throw new Exception("Old password is incorrect");
        }
        u.changePassword(this.security_controller.hashPassword(newPassword));
    }

    public void changeUsername(String userToken, String newUsername) throws Exception {
        if(!connectedUsers.containsKey(userToken))
        {
            throw new Exception("Invalid user token");
        }
        if(usersByName.containsKey(newUsername))
            throw new IllegalArgumentException("User name all ready in use.");
        User u = connectedUsers.get(userToken);
        if(!u.getIsLoggedIn())
            throw new IllegalArgumentException("only members can change user name.");
        String oldUsername = u.getUserName();
        u.changeUsername(newUsername);
        if(usersByName.containsKey(oldUsername)) {
            this.usersByName.remove(oldUsername);
            this.usersByName.put(newUsername, u);
        }
    }

    private boolean isValidPass(String pass, String userName)
    {
        return !pass.isBlank() && pass.length() >= 6 && (!pass.contains(userName));
    }

    public void sendQuestionsToStore(String userToken, String storeName, String message) throws Exception{
        if(!stores.containsKey(storeName))
        {
            throw new Exception("No such store "+ storeName);
        }
        IStore store = stores.get(storeName);
        User u = getConnectedUserByToken(userToken);
        String userName = u.getUserName();
        this.bus.addMessage(store, userName, message);
    }

    private User getConnectedUserByToken(String userToken) throws Exception
    {
        if(!connectedUsers.containsKey(userToken))
        {
            throw new Exception("Invalid user token");
        }
        return connectedUsers.get(userToken);
    }

    public void sendComplaint(String userToken, String msg) throws  Exception {
        for(User u : this.usersByName.values())
        {
            if(u.isAdmin())
            {
                bus.addMessage(u, msg);
                return;
            }
        }
        throw new Exception("This is a bug : No admin was found in the system");
    }

    public boolean isMemberLoggedOut(String userToken) throws Exception{
        User u = getConnectedUserByToken(userToken);
        return !u.getIsLoggedIn();

    }
}
