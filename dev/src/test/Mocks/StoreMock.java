package test.Mocks;

import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Stores.IStore;
import main.Stores.Product;
import main.Stores.StoreReview;
import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
import main.utils.Pair;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StoreMock implements IStore {

    private ConcurrentLinkedQueue<OwnerPermissions> ownerAppointments=new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<ManagerPermissions> managerAppointments = new ConcurrentLinkedQueue<>();

    @Override
    public List<User> getOwnersOfStore() {
        return null;
    }

    @Override
    public List<User> getManagersOfStore() {
        return null;
    }

    @Override
    public boolean addProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        return true;
    }

    @Override
    public boolean updateProduct(String oldProductName,String newProductName, String category, List<String> keyWords, String description, int quantity, double price){
        return true;
    }

    @Override
    public ConcurrentLinkedQueue<OwnerPermissions> getOwnersAppointments() {
        return ownerAppointments;
    }

    @Override
    public ConcurrentLinkedQueue<ManagerPermissions> getManagersAppointments() {
        return managerAppointments;
    }

    @Override
    public void addOwnerToStore(OwnerPermissions newOwnerAppointment) {

    }

    @Override
    public void addManager(ManagerPermissions newManagerAppointment) {

    }

    @Override
    public void removeManager(ManagerPermissions mp) {

    }

    @Override
    public void removeOwner(OwnerPermissions ow) {

    }

    @Override
    public void closeStore(NotificationBus bus) {

    }

    @Override
    public ConcurrentHashMap<String, Product> getProductsByName() {
        return null;
    }

    @Override
    public Product getProduct(String name) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Boolean getIsActive() {
        return null;
    }

    @Override
    public void reOpen(NotificationBus bus) {

    }

    @Override
    public HashMap<User, String> getStoreStaff() {
        return null;
    }

    @Override
    public boolean respondToBuyer(User toRespond, String msg, NotificationBus bus) {
        return false;
    }

    @Override
    public ConcurrentHashMap<ShoppingBasket, LocalDateTime> getPurchaseHistory() {
        return null;
    }

    @Override
    public void CancelStaffRoles() {

    }

    @Override
    public boolean removeProduct(String productName) {
        return true;
    }

    @Override
    public void purchaseBasket(NotificationBus bus, ShoppingBasket bask) throws Exception {

    }

    @Override
    public void addReview(StoreReview sReview) {

    }

    @Override
    public boolean ValidateProduct(Product key, Integer value) {
        return false;
    }
}
