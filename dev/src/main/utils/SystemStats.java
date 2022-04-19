package main.utils;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class SystemStats {
    private LocalDateTime date;
    private AtomicInteger numOfLoggedIn;
    private AtomicInteger numOfRegistered;
    private AtomicInteger numOfPurchases;

    public SystemStats(LocalDateTime date){
        this.date=date;
        numOfLoggedIn=new AtomicInteger(0);
        numOfRegistered=new AtomicInteger(0);
        numOfPurchases=new AtomicInteger(0);
    }

    public void addLogIn(){
        numOfLoggedIn.incrementAndGet();
    }
    public void addRegister(){
        numOfRegistered.incrementAndGet();
    }
    public void addPurchase(){
        numOfPurchases.incrementAndGet();
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
}
