package main.Market;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Persistence.DAO;
import main.Publisher.Publisher;
import main.Service.Configuration;
import main.Stores.Store;
import main.Users.User;
import main.utils.SystemStats;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MarketBuilder {
    private boolean shouldLoadFromDB;

    private ConcurrentHashMap<String, User> membersByUserName=null; //key=username
    private ConcurrentHashMap<String, Store> stores=null; //key=store name
    private ConcurrentHashMap <LocalDate, SystemStats> systemStatsByDate=null;
    private IPayment Psystem=null;
    private ISupplying Ssystem=null;
    private String adminUsername = null;
    private String adminPassword = null;

    public MarketBuilder(boolean shouldLoadFromDB){
        if(shouldLoadFromDB)
            DAO.enablePersist();
        this.shouldLoadFromDB=shouldLoadFromDB;
    }

    public MarketBuilder() {
    }

    public MarketBuilder loadUsers(){
        membersByUserName=new ConcurrentHashMap<>();
        if(shouldLoadFromDB) {
            List<User> users = DAO.getInstance().getUsers();
            for (User u : users) {
                if(u.getIsLoggedIn())
                    u.logout();
                u.setObserver(new Publisher(u,null));
                membersByUserName.put(u.getUserName(),u);
            }
        }
        return this;
    }

    public MarketBuilder setPSystem(IPayment Psystem){
        this.Psystem=Psystem;
        return this;
    }

    public MarketBuilder setSSystem(ISupplying Ssystem){
        this.Ssystem=Ssystem;
        return this;
    }

    public MarketBuilder loadStores(){
        stores=new ConcurrentHashMap<>();
        if(shouldLoadFromDB) {
            List<Store> strs = DAO.getInstance().getStores();
            for (Store store : strs) {
                stores.put(store.getName(),store);
            }
        }
        return this;
    }

    public MarketBuilder loadStats(){
        systemStatsByDate=new ConcurrentHashMap<>();
        if(shouldLoadFromDB) {
            List<SystemStats> stats = DAO.getInstance().getStats();
            for (SystemStats stat : stats) {
                systemStatsByDate.put(stat.getDate(),stat);
            }
        }
        return this;
    }

    public Market build(){
        Psystem = (Psystem == null) ? new PaymentAdapter() : Psystem;
        Ssystem = (Ssystem == null) ? new SupplyingAdapter() : Ssystem;
        membersByUserName = (membersByUserName ==null) ? new ConcurrentHashMap<>() : membersByUserName;
        stores = (stores == null) ? new ConcurrentHashMap<>() : stores;
        systemStatsByDate = (systemStatsByDate == null) ? new ConcurrentHashMap<>() : systemStatsByDate;
        adminUsername = (adminUsername == null) ? "admin" : adminUsername;
        adminUsername = (adminPassword == null) ? "admin" : adminPassword;
        return new Market(Psystem,Ssystem,membersByUserName,stores,systemStatsByDate, adminUsername, adminPassword);
    }

    public Market build(Configuration conf) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (conf.getPersistence_unit() != null)
            DAO.setPersistence_unit(conf.getPersistence_unit());
        if(conf.getShouldPersist()) {
            DAO.enablePersist();
            loadStats().loadUsers().loadStores();
        }
        else DAO.disablePersist();
        Psystem = (Psystem == null) ? new PaymentAdapter() : conf.getPaymentSystem().getDeclaredConstructor().newInstance();
        Ssystem = (Ssystem == null) ? new SupplyingAdapter() : conf.getSupplyingSystem().getDeclaredConstructor().newInstance();
        membersByUserName = (membersByUserName ==null) ? new ConcurrentHashMap<>() : membersByUserName;
        stores = (stores == null) ? new ConcurrentHashMap<>() : stores;
        systemStatsByDate = (systemStatsByDate == null) ? new ConcurrentHashMap<>() : systemStatsByDate;
        adminUsername = (conf.getAdminUsername() == null) ? "admin" : conf.getAdminUsername();
        adminPassword = (conf.getAdminPassword() == null) ? "admin" : conf.getAdminPassword();
        return new Market(Psystem,Ssystem,membersByUserName,stores,systemStatsByDate, adminUsername, adminPassword);
    }

}
