package main.utils;

import main.Publisher.*;
import main.Users.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class SystemStats {
    @Id
    private LocalDate date;
    private AtomicInteger numOfLoggedIn;
    private AtomicInteger numOfRegistered;
    private AtomicInteger numOfPurchases;
    private AtomicInteger numOfGuests;
    private AtomicInteger numOfNonStaffMembers;
    private AtomicInteger numOfStoreManagers;
    private AtomicInteger numOfStoreOwners;
    private AtomicInteger numOfSystemAdmins;

    @Transient
    private ConcurrentLinkedQueue<User> observingUsers;

    public SystemStats(LocalDate date){
        this.date=date;
        numOfLoggedIn=new AtomicInteger(0);
        numOfRegistered=new AtomicInteger(0);
        numOfPurchases=new AtomicInteger(0);
        numOfGuests=new AtomicInteger(0);
        numOfNonStaffMembers=new AtomicInteger(0);
        numOfStoreManagers=new AtomicInteger(0);
        numOfStoreOwners=new AtomicInteger(0);
        numOfSystemAdmins=new AtomicInteger(0);
        observingUsers=new ConcurrentLinkedQueue<>();
    }

    public SystemStats() {
        observingUsers=new ConcurrentLinkedQueue<>();
    }

    public void addLogIn(){
        numOfLoggedIn.incrementAndGet();
        Notification n = generateNotification();
        notifyObserver(n);
    }
    public void addRegister() {
        numOfRegistered.incrementAndGet();
        Notification n = generateNotification();
        notifyObserver(n);
    }
    public void addPurchase(){
        numOfPurchases.incrementAndGet();
        Notification n = generateNotification();
        notifyObserver(n);
    }

    public void addGuestVisitor(){
        numOfGuests.incrementAndGet();
        pushNotification();
    }

    public int getGuestsVisitors(){
        return numOfGuests.get();
    }

    public void addNonStaffVisitor(){
        numOfNonStaffMembers.incrementAndGet();
        pushNotification();
    }

    public int getNonStaffVisitors(){
        return numOfNonStaffMembers.get();
    }

    public void addManagerVisitor(){
        numOfStoreManagers.incrementAndGet();
        pushNotification();
    }

    public int getManagersVisitors(){
        return numOfStoreManagers.get();
    }

    public void addOwnerVisitor(){
        numOfStoreOwners.incrementAndGet();
        pushNotification();
    }

    public int getOwnerVisitors(){
        return numOfStoreOwners.get();
    }

    public void addAdminVisitor(){
        numOfSystemAdmins.incrementAndGet();
        pushNotification();
    }

    public int getAdminVisitors(){
        return numOfSystemAdmins.get();
    }

    private void pushNotification() {
        Notification n = generateNotification();
        notifyObserver(n);
    }

    private Notification generateNotification() {
        return new StatNotificationPush(numOfGuests.get(),numOfNonStaffMembers.get(),
                numOfStoreManagers.get(),numOfStoreOwners.get(),
                numOfSystemAdmins.get(),numOfLoggedIn.get(),
                numOfPurchases.get(),numOfRegistered.get());

    }

    public Integer getNumOfLoggedIn() {
        return numOfLoggedIn.get();
    }

    public Integer getNumOfPurchases() {
        return numOfPurchases.get();
    }

    public Integer getNumOfRegistered() {
        return numOfRegistered.get();
    }

    public LocalDate getDate() {
        return date;
    }


    public void registerObserver(User user) {
        this.observingUsers.add(user);
    }

    public boolean notifyObserver(Notification notification) {
        for(User user:observingUsers){
            user.getObserver().update(notification);
        }
        return true;
    }

    @Override
    public String toString() {
        return generateNotification().print();
    }

}
