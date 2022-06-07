package main.Market;


import io.javalin.websocket.WsContext;
import main.DTO.*;
import main.Persistence.DAO;
import main.Publisher.Notification;
import main.Publisher.PersonalNotification;
import main.Publisher.Publisher;
import main.Publisher.WebSocket;
import main.Stores.*;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.ProductReview;
import main.Stores.StoreReview;


import main.Logger.Logger;
import main.Security.ISecurity;
import main.Security.Security;
import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;
import main.Users.StorePermission;
import main.Users.User;

import main.utils.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    private ConcurrentHashMap<String, User> membersByUserName; //key=username
    private ConcurrentHashMap<String, User> connectedSessions; //key=userToken, generated randomly by system
    private ConcurrentHashMap<String, Store> stores; //key=store name
    private ISecurity security_controller;
    private IPayment Psystem;
    private ISupplying Ssystem;
    private DAO dao;

    private AtomicInteger currentlyLoggedInMembers;

    public void addBargainPolicy(String userToken, String storeName, String productName, Double originalPrice) {
        getConnectedUserByToken(userToken).addBargainPolicy(getDomainStoreByName(storeName), productName, originalPrice);
    }

    public boolean verifyAdmin(String username, String password) {
        User admin = membersByUserName.get(username);
        if(admin==null || !admin.isAdmin())
            return false;
        try{
            admin.LogIn(password,security_controller);
            admin.logout();
            return true;
        }
        catch (IllegalArgumentException e){
            return false;
        }
    }

    private enum StatsType{Register, Login, Purchase}
    private ConcurrentHashMap <LocalDate, SystemStats> systemStatsByDate;

    public Market(IPayment Psystem, ISupplying Isystem){
        dao=DAO.getInstance();
        membersByUserName =new ConcurrentHashMap<>();
        connectedSessions =new ConcurrentHashMap<>();
        stores=new ConcurrentHashMap<>();
        systemStatsByDate=new ConcurrentHashMap<>();
        security_controller = new Security();
        currentlyLoggedInMembers = new AtomicInteger(0);
        this.initialize(Psystem, Isystem);
    }

    public Market(IPayment Psystem, ISupplying Ssystem, ConcurrentHashMap<String, User> users, ConcurrentHashMap<String,Store> stores, ConcurrentHashMap<LocalDate,SystemStats> stats){
        dao=DAO.getInstance();
        this.membersByUserName=users;
        this.systemStatsByDate=stats;
        this.stores=stores;
        security_controller = new Security();
        currentlyLoggedInMembers = new AtomicInteger(0);
        connectedSessions =new ConcurrentHashMap<>();
        this.initialize(Psystem,Ssystem);
    }

    public List<StoreDTO> getAllStoresOf(String userToken) {
        User user = connectedSessions.get(userToken);
        if(user==null)
            throw new IllegalArgumentException("This user isn't logged in");
        List<Store> domainRes = user.getAllStoresIsStaff();
        List<StoreDTO> serviceRes = new LinkedList<>();
        for (Store s : domainRes) {
            serviceRes.add(new StoreDTO(s));
        }
        return serviceRes;
    }

    public boolean assignWStoUserToken(String userToken, WsContext ctx) {
        User u = getConnectedUserByToken(userToken);
        if(!membersByUserName.containsKey(u.getUserName()))
            throw new IllegalArgumentException("This is a guest, it doesn't get any notifications");
        u.registerObserver(new Publisher(u,new WebSocket(ctx)));
        return true;
    }

    public boolean leaveWSforUserToken(String userToken) {
        User u = getConnectedUserByToken(userToken);
        u.registerObserver(new Publisher(u,null));
        return true;
    }

    public String getLoggedInVSRegistered(String userToken) {
        User admin = getConnectedUserByToken(userToken);
        if (!admin.isAdmin())
            throw new IllegalArgumentException("Only admin can do that");
        return String.format("%d/%d are logged in right now", currentlyLoggedInMembers.get(), membersByUserName.size());
    }
    public void addRafflePolicy(String userToken, String storeName, String productName, Double price) {
        User user = getConnectedUserByToken(userToken);
        Store store = getDomainStoreByName(storeName);
        user.addRafflePolicy(store, productName, price);
    }

    public void addAuctionPolicy(String userToken, String storeName, String productName, Double price, LocalDate Until) {
        User user = getConnectedUserByToken(userToken);
        Store store = getDomainStoreByName(storeName);
        user.addAuctionPolicy(store, productName, price, Until,Psystem,Ssystem);
    }

    public void addNormalPolicy(String userToken, String storeName, String productName, Double price) {
        User user = getConnectedUserByToken(userToken);
        Store store = getDomainStoreByName(storeName);
        user.addNormalPolicy(store, productName, price);
    }

    public boolean bidOnProduct(String userToken,String storeName, String productName, Double costumePrice, PaymentInformation paymentInformation, SupplyingInformation supplyingInformation) {
        User user = getConnectedUserByToken(userToken);
        Store store = getDomainStoreByName(storeName);
        return user.bidOnProduct(store, productName, costumePrice, paymentInformation, supplyingInformation, Psystem, Ssystem);
    }


    /***
     * This function should be called on every system start up.
     * @return new unique user token.
     */
    public String ConnectGuest() {
        String new_token = generateToken();
        User new_guest = new User(new_token);
        connectedSessions.put(new_token, new_guest);
        Logger.getInstance().logEvent("Market", String.format("New guest connected %s.", new_guest.getUserName()));
        return new_token;
    }

    public UserDTO DisconnectGuest(String user_token) {
        if (!connectedSessions.containsKey(user_token)) {
            Logger.getInstance().logBug("Market", String.format("invalid user token attempted to disconnect %s", user_token));
            throw new IllegalArgumentException("user token isn't connected.");
        }
        if (connectedSessions.get(user_token).getIsLoggedIn()) {
            throw new IllegalArgumentException("user token is a a member not a guest.");
        }
        User leaving_user = connectedSessions.remove(user_token);
        Logger.getInstance().logEvent("Market", String.format("User %s left the system.", leaving_user.getUserName()));
        return new UserDTO(leaving_user);
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
            DAO.getInstance().merge(systemStats);
        }
        else
        {
            SystemStats newSystemStats = new SystemStats(date);
            DAO.getInstance().persist(newSystemStats);
            switch (type) {
                case Register -> newSystemStats.addRegister();
                case Login -> newSystemStats.addLogIn();
                case Purchase -> newSystemStats.addPurchase();
            }
            DAO.getInstance().merge(newSystemStats);
            this.systemStatsByDate.put(date, newSystemStats);
        }
    }

    public boolean Register(String userName, String password) {
        if (membersByUserName.containsKey(userName)) {
            throw new IllegalArgumentException("username is taken.");
        }
        if (!security_controller.isValidPassword(password,userName)) {
            throw new IllegalArgumentException("password is not secure enough.");
        }
        User new_user = new User(false, userName, security_controller.hashPassword(password));
        dao.persist(new_user);

        membersByUserName.put(userName, new_user);
        Logger.getInstance().logEvent("Market", String.format("New user registered with username: %s", userName));
        addStats(StatsType.Register);
        return true;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public UserDTO Login(String token, String userName, String password) {
        if (!membersByUserName.containsKey(userName)) {
            throw new IllegalArgumentException("username doesn't exist.");
        }
        if (!connectedSessions.containsKey(token)) {
            Logger.getInstance().logBug("Market", String.format("token %s isn't in the system and attempted to log in.", token));
            throw new IllegalArgumentException("token isn't connected in the system.");
        }
        User u = membersByUserName.get(userName);
        Logger.getInstance().logEvent("Market", String.format("%s logged in.", userName));
        u.LogIn(password, this.security_controller);
        connectedSessions.put(token, u);
        addStats(StatsType.Login);
        dao.merge(u);
        return new UserDTO(u);
    }

    private Store getDomainStoreByName(String name) {
        if(!stores.containsKey(name))
            throw new IllegalArgumentException("Requested store doesn't exist.");
        return this.stores.get(name);
    }

    public StoreDTO getStoreByName(String name) {
        if(!stores.containsKey(name))
            throw new IllegalArgumentException("Requested store doesn't exist.");
        return new StoreDTO(this.stores.get(name));
    }

    public List<String> getStoresByString(String name) {
        List<String> res = new LinkedList<>();
        for (String key : this.stores.keySet()) {
            if (stringFunctions.calculate_distance(name, key) <= 3)
                res.add(key);
        }
        return res;
    }

    public List<ProductDTO> getStoreProducts(String storeName) {
        List<ProductDTO> res = new LinkedList<>();
        StoreDTO st = this.getStoreByName(storeName);
        if (st == null)
            throw new IllegalArgumentException("store doesn't exist.");
        for (String productName : st.getProductsByName().keySet())
            res.add(st.getProductsByName().get(productName));
        return res;
    }

    public List<ProductDTO> getProductsByAttributes(String productName, String category, String keyWord, Double productRating, Double storeRating, Double minPrice, Double maxPrice){
        List<ProductDTO> result = new LinkedList<>();
        for (Store currStr : this.stores.values())
            for (Product currPrd : currStr.getProductsByName().values()) {
                if (productName == null ||productName.isBlank()|| currPrd.getName().equals(productName))
                    if (category == null ||category.isBlank()|| currPrd.getCategory().equals(category))
                        if (keyWord == null || keyWord.isBlank()|| currPrd.hasKeyWord(keyWord))
                            if (productRating == null) //TODO: || rating = productRating
                                if (storeRating == null) //TODO: || rating = productRating
                                    if (minPrice == null || maxPrice == null || (currPrd.getCleanPrice() <= maxPrice && currPrd.getCleanPrice() >= minPrice))
                                        result.add(new ProductDTO(currPrd));
            }
        return result;
    }

    public boolean addProductToCart(String userToken, String storeName, String productName, int quantity) {
        User us = this.connectedSessions.get(userToken);
        if (quantity <= 0)
            throw new IllegalArgumentException("quantity is lesss than or equal to 0.");
        if (us == null) {
            Logger.getInstance().logBug("Market", String.format("Unknown user token, %s.", userToken));
            throw new IllegalArgumentException("Unkown user token.");
        }
        Store st = this.getDomainStoreByName(storeName);
        if(st == null) {
            throw new IllegalArgumentException("Store doesn't exist.");
        }
        if(!st.isProductAddable(productName))
            throw new IllegalArgumentException("Can't add this product to the cart - its policy doesn't allow to do so");
        boolean output= us.addProductToCart(st, productName, quantity);
        dao.merge(us);
        return output;
    }

    public boolean addProductToCart(String userToken, String storeName, String productName, double price) throws Exception{
        if(price <= 0)
            throw new IllegalArgumentException("can't pay 0 or less.");
        User user = getConnectedUserByToken(userToken);
        Store store = getDomainStoreByName(storeName);
        boolean output= user.addProductToCart(store, productName, price);
        dao.merge(user);
        return output;
    }

    public boolean RemoveProductFromCart(String userToken, String storeName, String productName, int quantity) {
        User us = this.connectedSessions.get(userToken);
        if (quantity <= 0)
            throw new IllegalArgumentException("quantity is lesss than or equal to 0.");
        if (us == null) {
            Logger.getInstance().logBug("Market", String.format("Unknown user token, %s.", userToken));
            throw new IllegalArgumentException("Unkown user token.");
        }
        Store st = this.getDomainStoreByName(storeName);
        if(st == null) {
            throw new IllegalArgumentException("Store doesn't exist.");
        }
        boolean output= us.RemoveProductFromCart(st, productName, quantity);
        dao.merge(us);
        return output;
    }

    public ShoppingCartDTO getUserCart(String userToken) {
        User us = this.connectedSessions.get(userToken);
        if (us == null) {
            Logger.getInstance().logBug("Market", String.format("Unknown user token, %s.", userToken));
            throw new IllegalArgumentException("Unkown user token.");
        }
        return new ShoppingCartDTO(us.getCart(), us);
    }


    public boolean addProductToStore(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        boolean output= p.first.addProductToStore(p.second,productName,category,keyWords,description,quantity,price);
        dao.merge(p.first);
        return output;
    }

    public boolean updateProductInStore(String userToken, String oldProductName,String newProductName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        boolean output= p.first.updateProductToStore(p.second, oldProductName,newProductName, category, keyWords, description, quantity, price);
        dao.merge(p.second);
        return output;
    }

    public boolean appointStoreOwner(String userToken, String userToAppoint, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        User user_to_appoint = membersByUserName.get(userToAppoint);
        if (user_to_appoint == null)
            throw new IllegalArgumentException("The user to appoint doesn't exist!");

        return p.first.appointOwnerToStore(p.second, user_to_appoint);
    }

    public boolean removeStoreOwnerAppointment(String userToken, String userAppointed, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        User appointed_user = membersByUserName.get(userAppointed);
        if (appointed_user == null)
            throw new IllegalArgumentException("The user appointed doesn't exist in the system");

        return p.first.removeOwnerAppointment(p.second, appointed_user);
    }

    private Pair<User, Store> getConnectedUserAndStore(String userToken, String storeName) {
        User user = connectedSessions.get(userToken);
        if (user == null)
            throw new IllegalArgumentException("User not logged in");

        Store Store = stores.get(storeName);
        if (Store == null)
            throw new IllegalArgumentException("No such store");

        return new Pair<>(user, Store);
    }

    public boolean appointStoreManager(String userToken, String userToAppoint, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        User user_to_appoint = membersByUserName.get(userToAppoint);
        if (user_to_appoint == null)
            throw new IllegalArgumentException("The user to appoint doesn't exist!");
        if (p.first == user_to_appoint)
            throw new IllegalArgumentException("Can't appoint yourself!");

        return p.first.appointManagerToStore(p.second, user_to_appoint);
    }

    private boolean allowOrDisallowPermission(String userToken, String managerName, String storeName, StorePermission permission, boolean shouldGrant) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        User manager = membersByUserName.get(managerName);
        if (manager == null)
            throw new IllegalArgumentException("There's no such manager");

        return p.first.grantOrDeletePermission(manager, p.second, shouldGrant, permission);
    }

    public boolean allowManagerBargainProducts(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.BargainPermission, true);
    }

    public boolean disallowManagerBargainProducts(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.BargainPermission, false);
    }

    public boolean allowManagerPolicyProducts(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.PolicyPermission, true);
    }

    public boolean disallowManagerPolicyProducts(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken, managerName, storeName, StorePermission.PolicyPermission, false);
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
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        User manager = membersByUserName.get(userAppointed);
        if (manager == null)
            throw new IllegalArgumentException("The user doesn't exist in the system!");

        return p.first.removeManagerAppointment(p.second, manager);
    }

    public boolean closeStore(String userToken, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.closeStore(p.second);
    }

    public boolean reopenStore(String userToken, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.reOpenStore(p.second);
    }

    public HashMap<User, String> getStoreStaff(String userToken, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.getStoreStaff(p.second);
    }

    public List<String> receiveQuestionsFromBuyers(String userToken, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.receiveQuestionsFromStore(p.second);
    }

    public boolean sendRespondToBuyer(String userToken, String storeName, String userToRespond, String msg) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        User toRespond = membersByUserName.get(userToRespond);
        if (toRespond == null)
            throw new IllegalArgumentException("No such user to respond to");
        return p.first.sendRespondFromStore(p.second, toRespond, msg);
    }

    public Map<ShoppingBasketDTO, LocalDateTime> getStorePurchaseHistory(String userToken, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        return p.first.getStorePurchaseHistoryByTime(p.second);
    }

    public boolean deleteStore(String userToken, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        if (p.first.removeStore(p.second)) {
            stores.remove(storeName);
            return true;
        }
        return false;
    }

    public boolean deleteUser(String userToken, String userName) {

        User admin = connectedSessions.get(userToken);
        if (admin == null)
            throw new IllegalArgumentException("No such admin in the system");
        User toDelete = membersByUserName.get(userName);

        // Check to see if user has any role
        if(toDelete.isAdmin() || toDelete.isManager() || toDelete.isFounder() || toDelete.isOwner())
            throw new IllegalArgumentException("Cant delete a user with a role");
        membersByUserName.remove(toDelete.getUserName());
        DAO.getInstance().remove(toDelete);
        return true;
    }

    public List<String> receiveMessages(String userToken) {
        User user = connectedSessions.get(userToken);
        if (user == null)
            throw new IllegalArgumentException("User isn't connected");
         LinkedList<Notification> lst = user.getAllNotifications();
         LinkedList<String> output = new LinkedList<>();
         for(Notification n : lst)
             output.add(n.print());
        return output;
    }

    public boolean respondToMessage(String userToken, String userToRespond, String msg) {
        User responding_user = connectedSessions.get(userToken);
        if(!responding_user.isAdmin())
            throw new IllegalArgumentException("Only admin can respond to messages");
        User user_receiving_msg = membersByUserName.get(userToRespond);
        if(user_receiving_msg==null)
            throw new IllegalArgumentException("No such user to respond to");
        Notification n =new PersonalNotification(responding_user.getUserName(),msg);
        DAO.getInstance().persist(n);
        user_receiving_msg.notifyObserver(n);
        return true;
    }

    public String getNumberOfLoggedInUsersPerDate(String userToken, LocalDate date) {
        return String.valueOf(getStats(userToken, date).getNumOfLoggedIn());
    }

    private SystemStats getStats(String userToken, LocalDate date) {
        User admin = connectedSessions.get(userToken);
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
    private void initialize(IPayment Psystem, ISupplying Isystem) {
        String adminUserName = "admin";
        String adminHashPassword = security_controller.hashPassword("admin");
        if(!membersByUserName.containsKey(adminUserName)) {
            User admin = new User(true, adminUserName, adminHashPassword);
            dao.persist(admin);
            membersByUserName.put("admin", admin);
            Logger.getInstance().logEvent("Market", String.format("Added Default system admin with username: %s", adminUserName));
        }
        setSsystem(Isystem);
        setPsystem(Psystem);
    }

    public void setPsystem(IPayment psystem) {
        Psystem = psystem;
    }

    public void setSsystem(ISupplying ssystem) {
        Ssystem = ssystem;
    }

    public boolean openStore(String userToken, String storeName) throws Exception{
        User founder = connectedSessions.get(userToken);
        synchronized (stores) {
            if(founder== null)
            {
                throw new Exception("Invalid user token");
            }
            if (!membersByUserName.containsKey(founder.getUserName()))
                throw new IllegalArgumentException("This user isn't registered to the system!");
            if (stores.containsKey(storeName))
                throw new IllegalArgumentException("There's already a store with that name in the system");
        Store newStore =founder.openStore(storeName);
        stores.put(storeName, newStore);

        dao.persist(newStore);
        dao.merge(founder);
        }
        return true;
    }

    public boolean removeProductFromStore(String userToken, String productName, String storeName) {
        Pair<User, Store> p = getConnectedUserAndStore(userToken, storeName);
        boolean output= p.first.removeProductFromStore(productName,p.second);
        dao.merge(p.first);
        return output;
    }

    public void addSecurityQuestion(String userToken, String question, String answer) throws Exception
    {
        if(question.isBlank() || answer.isBlank())
        {
            throw new IllegalArgumentException("Answer and Question cant be empty");
        }
        User u = getConnectedUserByToken(userToken);
        if(!membersByUserName.containsKey(u.getUserName()))
        {
            throw new Exception("User is a not a member");
        }
        u.addSecurityQuestion(question, answer);
        dao.merge(u);
    }

    public void logout(String token) throws Exception
    {
        User u = getConnectedUserByToken(token);
        u.logout();
        dao.merge(u);
        connectedSessions.put(token,new User(token));
    }

    public void purchaseCart(String userToken, PaymentInformation pinfo, SupplyingInformation sinfo) throws Exception
    {
        //User purchase history update
        User u = getConnectedUserByToken(userToken);
        u.purchaseCart(pinfo, sinfo, this.Psystem, this.Ssystem);
        dao.merge(u);
        addStats(StatsType.Purchase);
    }

    public List<ShoppingCartDTO> getPurchaseHistory(String userToken, String userName) throws Exception{
        User u = getConnectedUserByToken(userToken);
        if(!u.getIsLoggedIn())
        {
            throw new IllegalArgumentException("User is not logged in");
        }
        if(!(u.isAdmin() || u.getUserName().equals(userName)))
            throw new IllegalArgumentException("No permission to ask for that purchase history.");
        User uToReturn = membersByUserName.get(userName);
        if(uToReturn == null)
            throw new IllegalArgumentException("User doesn't exist.");
        return u.getPurchaseHistory();

    }

    public void writeProductReview(String userToken, String productName, String storeName, String reviewDescription, double points){
        User u = getConnectedUserByToken(userToken);
        if(!u.isProductInHistoryByNameAndStore(productName, storeName))
            throw new IllegalArgumentException("Product was not found in user's purchase history");

        Product prod = getProductByNameAndStore(productName, storeName);
        ProductReview pReview = new ProductReview(u, prod, reviewDescription, points);
        prod.addReview(pReview);
        dao.persist(pReview);
        dao.merge(prod);
    }

    private Product getProductByNameAndStore(String productName, String storeName) {
        if(!this.stores.containsKey(storeName))
            throw new IllegalArgumentException("No such store in history");
        Store store = this.stores.get(storeName);
        return store.getProduct(productName);
    }

    public void writeStoreReview(String userToken, String storeName, String reviewDescription, double points) throws Exception{
        User u = getConnectedUserByToken(userToken);
        if(!u.getIsLoggedIn())
            throw new IllegalArgumentException("Only members can write reviews.");
        if(!u.isStoreInHistory(storeName))
            throw new IllegalArgumentException("Store was not found in user's purchase history");

        Store store = stores.get(storeName);
        StoreReview sReview = new StoreReview(u, store, reviewDescription, points);
        store.addReview(sReview);
        dao.persist(sReview);
        dao.merge(store);
    }

    public void changePassword(String userToken, String oldPassword, String newPassword){
        User u = getConnectedUserByToken(userToken);
        u.changePassword(newPassword, this.security_controller, oldPassword);
        dao.merge(u);
    }

    public void changeUsername(String userToken, String newUsername) throws Exception {
        if(!connectedSessions.containsKey(userToken))
        {
            throw new Exception("Invalid user token");
        }
        if(membersByUserName.containsKey(newUsername))
            throw new IllegalArgumentException("User name all ready in use.");
        User u = connectedSessions.get(userToken);
        if(!u.getIsLoggedIn())
            throw new IllegalArgumentException("only members can change user name.");
        String oldUsername = u.getUserName();
        u.changeUsername(newUsername);
        if(membersByUserName.containsKey(oldUsername)) {
            this.membersByUserName.remove(oldUsername);
            this.membersByUserName.put(newUsername, u);
        }
        dao.merge(u);
    }

    public void sendQuestionsToStore(String userToken, String storeName, String message) throws Exception{
        if(!stores.containsKey(storeName))
        {
            throw new IllegalArgumentException("No such store "+ storeName);
        }
        if(message.isBlank())
        {
            throw new IllegalArgumentException("Illegal message body");
        }
        Store store = stores.get(storeName);
        User u = getConnectedUserByToken(userToken);
        String userName = u.getUserName();
        store.addQuestionToStore(userName,message);
        DAO.getInstance().merge(store);
    }

    private User getConnectedUserByToken(String userToken)
    {
        if(!connectedSessions.containsKey(userToken))
        {
            throw new IllegalArgumentException("Invalid user token");
        }
        return connectedSessions.get(userToken);
    }

    public void sendComplaint(String userToken, String msg) throws  Exception {
        if(msg.isBlank())
        {
            throw new IllegalArgumentException("Illegal message body");
        }
        User user = getConnectedUserByToken(userToken);
        if(getPurchaseHistory(userToken, user.getUserName()).isEmpty())
        {
            throw new IllegalArgumentException("User has no purchase history. Cant send complaint without purchasing any product");
        }
        for(User u : this.membersByUserName.values())
        {
            if(u.isAdmin())
            {
                Notification n =new PersonalNotification(user.getUserName(),msg);
                dao.persist(n);
                u.notifyObserver(n);
                dao.merge(u);
                return;
            }
        }
        throw new Exception("This is a bug : No admin was found in the system");
    }

    public boolean isMemberLoggedOut(String userToken){
        User u = getConnectedUserByToken(userToken);
        return !u.getIsLoggedIn();

    }

    public void addDiscountPasswordToBasket(String userToken, String storeName, String Password) throws  Exception {
        getConnectedUserByToken(userToken).addDiscountPasswordToBasket(storeName, Password);
    }

    public List<BidDTO> getUserBids(String userToken, String storeName, String productName){
        List<Bid> toDTO = getConnectedUserByToken(userToken).getUserBids(getDomainStoreByName(storeName), productName);
        List<BidDTO> output = new LinkedList<>();
        for(Bid bid : toDTO)
            output.add(new BidDTO(bid));
        return output;
    }

    public void ApproveBid(String userToken, String storeName, String productName, String username) throws Exception {
        User user = membersByUserName.get(username);
        getConnectedUserByToken(userToken).ApproveBid(getDomainStoreByName(storeName), productName, user,Psystem,Ssystem);
    }

    public void DeclineBid(String userToken, String storeName, String productName, String username) throws Exception {
        User user = membersByUserName.get(username);
        getConnectedUserByToken(userToken).DeclineBid(getDomainStoreByName(storeName), productName, user);
    }

    public void CounterOfferBid(String userToken, String storeName, String productName, String username, Double offer) throws Exception {
        User user = membersByUserName.get(username);
        getConnectedUserByToken(userToken).CounterOfferBid(getDomainStoreByName(storeName), productName, user, offer);
    }

    public int CreateSimpleDiscount(String userToken, String store, LocalDate until, Double percent){
        return getConnectedUserByToken(userToken).CreateSimpleDiscount(getDomainStoreByName(store), until, percent);
    }
    public int CreateSecretDiscount(String userToken, String store, LocalDate until, Double percent, String secretCode){
        return getConnectedUserByToken(userToken).CreateSecretDiscount(getDomainStoreByName(store), until, percent, secretCode);
    }
    public int CreateConditionalDiscount(String userToken, String store, LocalDate until, Double percent, int condID){
        return getConnectedUserByToken(userToken).CreateConditionalDiscount(getDomainStoreByName(store), until, percent, condID);
    }
    public int CreateMaximumCompositeDiscount(String userToken, String store, LocalDate until, List<Integer> discounts){
        return getConnectedUserByToken(userToken).CreateMaximumCompositeDiscount(getDomainStoreByName(store), until, discounts);
    }
    public int CreatePlusCompositeDiscount(String userToken, String store, LocalDate until, List<Integer> discounts){
        return getConnectedUserByToken(userToken).CreatePlusCompositeDiscount(getDomainStoreByName(store), until, discounts);
    }

    public void SetDiscountToProduct(String userToken, String store, int discountID, String productName){
        getConnectedUserByToken(userToken).SetDiscountToProduct(getDomainStoreByName(store), discountID, productName);
    }
    public void SetDiscountToStore(String userToken, String store, int discountID){
        getConnectedUserByToken(userToken).SetDiscountToStore(getDomainStoreByName(store), discountID);
    }

    public int CreateBasketValueCondition(String userToken, String store, double requiredValue){
        return getConnectedUserByToken(userToken).CreateBasketValueCondition(getDomainStoreByName(store), requiredValue);
    }
    public int CreateCategoryAmountCondition(String userToken, String store, String category, int amount){
        return getConnectedUserByToken(userToken).CreateCategoryAmountCondition(getDomainStoreByName(store), category, amount);
    }
    public int CreateProductAmountCondition(String userToken, String store, String productName, int amount){
        return getConnectedUserByToken(userToken).CreateProductAmountCondition(getDomainStoreByName(store), productName, amount);
    }
    public int CreateLogicalAndCondition(String userToken, String store, List<Integer> conditionIds){
        return getConnectedUserByToken(userToken).CreateLogicalAndCondition(getDomainStoreByName(store), conditionIds);
    }
    public int CreateLogicalOrCondition(String userToken, String store, List<Integer> conditionIds){
        return getConnectedUserByToken(userToken).CreateLogicalOrCondition(getDomainStoreByName(store), conditionIds);
    }
    public int CreateLogicalXorCondition(String userToken, String store, int id1, int id2){
        return getConnectedUserByToken(userToken).CreateLogicalXorCondition(getDomainStoreByName(store), id1, id2);
    }
    public void SetConditionToDiscount(String userToken, String store, int discountId, int ConditionID){
        getConnectedUserByToken(userToken).SetConditionToDiscount(getDomainStoreByName(store), discountId, ConditionID);
    }

    public void SetConditionToStore(String userToken, String store, int ConditionID){
        getConnectedUserByToken(userToken).SetConditionToStore(getDomainStoreByName(store), ConditionID);
    }

    public void setMembersByUserName(ConcurrentHashMap<String, User> membersByUserName) {
        this.membersByUserName = membersByUserName;
    }

    public void setConnectedSessions(ConcurrentHashMap<String, User> connectedSessions) {
        this.connectedSessions = connectedSessions;
    }

    public void setStores(ConcurrentHashMap<String, Store> stores) {
        this.stores = stores;
    }

    public void setSecurity_controller(ISecurity security_controller) {
        this.security_controller = security_controller;
    }
}