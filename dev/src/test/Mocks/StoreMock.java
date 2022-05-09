package test.Mocks;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Stores.IStore;
import main.Stores.Product;
import main.Stores.StoreReview;
import main.Users.ManagerPermissions;
import main.Users.OwnerPermissions;
import main.Users.User;
import main.utils.*;

import java.time.LocalDate;
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
    public void purchaseBasket(User user, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment, NotificationBus bus, ShoppingBasket bask) {

    }


    @Override
    public void addReview(StoreReview sReview) {

    }

    @Override
    public void notifyBargainingStaff(Bid newbid, NotificationBus bus) {

    }

    @Override
    public void addDirectDiscount(String productName, LocalDate until, Double percent) {

    }

    @Override
    public void addSecretDiscount(String productName, LocalDate until, Double percent, String secretCode) {

    }

    @Override
    public void addConditionalDiscount(String productName, LocalDate until, HashMap<Restriction, Double> restrictions) {

    }

    @Override
    public void addRafflePolicy(String productName, Double price, NotificationBus bus) {

    }

    @Override
    public void addAuctionPolicy(String productName, Double price, NotificationBus bus, LocalDate until) {

    }

    @Override
    public void addNormalPolicy(String productName, Double price, NotificationBus bus) {

    }

    @Override
    public boolean bidOnProduct(String productName, Bid bid, NotificationBus bus) {
        return true;
    }

    @Override
    public void addBargainPolicy(String productName, Double originalPrice, NotificationBus bus) {

    }
}
