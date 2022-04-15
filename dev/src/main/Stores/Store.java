package main.Stores;

import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Store implements IStore {

    private ConcurrentHashMap<String,Product> productsByName;
    private ConcurrentLinkedQueue<OwnerPermissions> owners;
    private ConcurrentLinkedQueue<ManagerPermissions> managers;
    public List<User> getOwnersOfStore(){
        LinkedList<User> storeOwners=new LinkedList<>();
        for(OwnerPermissions ow:owners){
            storeOwners.add(ow.getAppointedToOwner());
        }
        return storeOwners;
    }
    public List<User> getManagersOfStore(){
        LinkedList<User> storeManagers=new LinkedList<>();
        for(ManagerPermissions mp:managers){
            storeManagers.add(mp.getAppointedToManager());
        }
        return storeManagers;
    }

    public Store(){
        this.owners=new ConcurrentLinkedQueue<>();
        this.managers=new ConcurrentLinkedQueue<>();
        this.productsByName=new ConcurrentHashMap<>();
    }

    public boolean addProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if(productsByName.containsKey(productName))
            throw new IllegalArgumentException("There's already such product with this name in the store");

        Product product=new Product(productName,category,keyWords,description,quantity,price);
        productsByName.put(productName,product);
        return true;
    }

    public boolean updateProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        Product product=productsByName.get(productName);
        if(product==null)
            throw new IllegalArgumentException("No such product in the store!");
        String prevName=product.getName();
        if(!prevName.equals(productName)) { //name is changed
            if(productsByName.containsKey(productName)) //the name is already taken
                throw new IllegalArgumentException("There's already a product with that name!");
        }
        product.setProperties(productName,category,keyWords,description,quantity,price);

        productsByName.remove(prevName);
        productsByName.put(productName,product);
        return true;
    }

    public ConcurrentLinkedQueue<OwnerPermissions> getOwnersAppointments() {
        return owners;
    }

    public ConcurrentLinkedQueue<ManagerPermissions> getManagersAppointments() {
        return managers;
    }

    public void addOwnerToStore(OwnerPermissions newOwnerAppointment) {
        owners.add(newOwnerAppointment);
    }

    public void addManager(ManagerPermissions newManagerAppointment) {
        managers.add(newManagerAppointment);
    }
}
