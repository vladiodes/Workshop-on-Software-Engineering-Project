package main;

import main.Security.ISecurity;
import main.Stores.Store;
import main.Users.User;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Market {

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
    }

    public boolean addProductToStore(String userToken, String productName, String category, List<String> keyWords, String description, String storeName, int quantity, double price) {
        User user=connectedUsers.get(userToken);
        if(user==null)
            return false;

        Store store=stores.get(storeName);
        if(store==null)
            return false;

        return user.addProductToStore(store,productName,category,keyWords,description,quantity,price);
    }

}
