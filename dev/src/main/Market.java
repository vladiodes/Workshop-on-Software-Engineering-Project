package main;

import main.Logger.Logger;
import main.Security.ISecurity;

import main.Security.Security;
import main.Stores.Product;
import main.Stores.Store;
import main.Users.StorePermission;
import main.Users.User;
import main.utils.Pair;
import main.utils.stringFunctions;
import org.junit.platform.commons.util.StringUtils;

import javax.naming.NoPermissionException;
import javax.security.auth.login.LoginException;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.LinkedList;
import java.util.List;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Market {

    /**
     * usersByName is a hashmap that maps username -> User
     * each username is unique in the system
     *
     * connectedUsers is a hashmap that maps token -> connected user
     * once a user disconnects he's removed from this hashmap
     * the token is generated by the system, randomly
     */
    private ConcurrentHashMap<String, User> usersByName; //key=username
    private ConcurrentHashMap<String,User> connectedUsers; //key=userToken, generated randomly by system
    private ConcurrentHashMap<String, Store> stores; //key=store name
    private ISecurity security_controller;
    private AtomicInteger guestCounter;
    public Market(){
        usersByName=new ConcurrentHashMap<>();
        connectedUsers=new ConcurrentHashMap<>();
        stores=new ConcurrentHashMap<>();
        guestCounter=new AtomicInteger(1);
        security_controller = new Security();
    }

    /***
     * This function should be called on every system start up.
     * @return new unique user token.
     */
    public String ConnectGuest(){
        String new_token = generateToken();
        User new_guest = new User(new_token);
        connectedUsers.put(new_token, new_guest);
        Logger.getInstance().logEvent("Market", String.format("New guest connected %s.", new_guest.getUserName()));
        return new_token;
    }

    public User DisconnectGuest(String user_token){
        if (!connectedUsers.containsKey(user_token)){
            Logger.getInstance().logBug("Market", String.format("invalid user token attempted to disconnect %s", user_token));
            throw new IllegalArgumentException("user token isn't connected.");
        }
        if(connectedUsers.get(user_token).getIsLoggedIn()){
            throw new IllegalArgumentException("user token is a a member not a guest.");
        }
        User leaving_user = connectedUsers.remove(user_token);
        Logger.getInstance().logEvent("Market", String.format("User %s left the system.", leaving_user.getUserName()));
        return leaving_user;
    }

    public boolean Register(String userName, String password){
        if (usersByName.containsKey(userName)){
            throw new IllegalArgumentException("username is taken.");
        }
        if (password.length() < 6 || password.contains(userName)){
            throw new IllegalArgumentException("password is not secure enough.");
        }
        User new_user = new User(false, userName, security_controller.hashPassword(password));
        usersByName.put(userName, new_user);
        Logger.getInstance().logEvent("Market", String.format("New user registered with username: %s", userName));
        return true;
    }

    private String generateToken(){
        return UUID.randomUUID().toString();
    }

    public User Login(String token, String userName, String password){
        if (!usersByName.containsKey(userName)){
            throw new IllegalArgumentException("username doesn't exist.");
        }
        if (!connectedUsers.containsKey(token)){
            Logger.getInstance().logBug("Market", String.format("token %s isn't in the system and attempted to log in."));
            throw new IllegalArgumentException("token isn't connected in the system.");
        }
        if (connectedUsers.get(token).getIsLoggedIn()){
            throw new IllegalArgumentException("user already logged in.");
        }
        User u = usersByName.get(userName);
        if (!u.getHashed_password().equals(security_controller.hashPassword(password))) {
            throw new IllegalArgumentException("Incorrect password.");
        }
        Logger.getInstance().logEvent("Market", String.format("%s logged in.",userName));
        connectedUsers.put(token, u);
        u.LogIn();
        return u;
    }

    public Store getStoreByName(String name) {
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
        Store st = this.getStoreByName(storeName);
        if (st == null)
            throw new IllegalArgumentException("store doesn't exist.");
        for (String productName : st.getProductsByName().keySet())
            res.add(st.getProductsByName().get(productName));
        return res;
    }

    public List<Product> getProductsByAttributes(String productName, String category, String keyWord, Double productRating, Double storeRating, Double minPrice, Double maxPrice){
        List<Product> result = new LinkedList<>();
        for (Store currStr : this.stores.values())
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

    public boolean addProductToStore(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) throws NoPermissionException {
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        return p.first.addProductToStore(p.second,productName,category,keyWords,description,quantity,price);
    }

    public boolean updateProductInStore(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) throws NoPermissionException {
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        return p.first.updateProductToStore(p.second,productName,category,keyWords,description,quantity,price);
    }


    public boolean appointStoreOwner(String userToken, String userToAppoint, String storeName) {
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        User user_to_appoint=usersByName.get(userToAppoint);
        if(user_to_appoint==null)
            throw new IllegalArgumentException("The user to appoint doesn't exist!");

        return p.first.appointOwnerToStore(p.second,user_to_appoint);
    }



    public boolean removeStoreOwnerAppointment(String userToken, String userAppointed, String storeName) {
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        User appointed_user = usersByName.get(userAppointed);
        if(appointed_user==null)
            throw new IllegalArgumentException("The user appointed doesn't exist in the system");

        return p.first.removeOwnerAppointment(p.second,appointed_user);
    }

    private Pair<User,Store> getConnectedUserAndStore(String userToken,String storeName){
        User user= connectedUsers.get(userToken);
        if(user==null)
            throw new IllegalArgumentException("User not logged in");

        Store store = stores.get(storeName);
        if (store == null)
            throw new IllegalArgumentException("No such store");

        return new Pair<>(user,store);
    }

    public boolean appointStoreManager(String userToken, String userToAppoint, String storeName) {
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        User user_to_appoint=usersByName.get(userToAppoint);
        if(user_to_appoint==null)
            throw new IllegalArgumentException("The user to appoint doesn't exist!");
        if(p.first==user_to_appoint)
            throw new IllegalArgumentException("Can't appoint yourself!");

        return p.first.appointManagerToStore(p.second,user_to_appoint);
    }

    private boolean allowOrDisallowPermission(String userToken, String managerName, String storeName, StorePermission permission,boolean shouldGrant){
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        User manager=usersByName.get(managerName);
        if(manager==null)
            throw new IllegalArgumentException("There's no such manager");

        return p.first.grantOrDeletePermission(manager,p.second,shouldGrant,permission);
    }

    public boolean allowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken,managerName,storeName,StorePermission.UpdateAddProducts,true);
    }

    public boolean disallowManagerUpdateProducts(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken,managerName,storeName,StorePermission.UpdateAddProducts,false);
    }

    public boolean allowManagerViewPurchaseHistory(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken,managerName,storeName,StorePermission.ViewStoreHistory,true);
    }

    public boolean disallowManagerViewPurchaseHistory(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken,managerName,storeName,StorePermission.ViewStoreHistory,false);
    }

    public boolean allowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken,managerName,storeName,StorePermission.AnswerAndTakeRequests,true);
    }

    public boolean disallowManagerAnswerAndTakeRequests(String userToken, String managerName, String storeName) {
        return allowOrDisallowPermission(userToken,managerName,storeName,StorePermission.AnswerAndTakeRequests,false);
    }

    public boolean removeStoreManager(String userToken, String userAppointed, String storeName) {
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        User manager = usersByName.get(userAppointed);
        if(manager==null)
            throw new IllegalArgumentException("The user doesn't exist in the system!");

        return p.first.removeManagerAppointment(p.second,manager);

    }

    public boolean closeStore(String userToken, String storeName) {
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        return p.first.closeStore(p.second);

    }

    public boolean reOpenStore(String userToken, String storeName) {
        Pair<User, Store> p=getConnectedUserAndStore(userToken,storeName);
        return p.first.reOpenStore(p.second);
    }
}


