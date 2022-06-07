package main.utils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class SystemStats {
    @Id
    private LocalDate date;
    private AtomicInteger numOfLoggedIn;
    private AtomicInteger numOfRegistered;
    private AtomicInteger numOfPurchases;

    public SystemStats(LocalDate date){
        this.date=date;
        numOfLoggedIn=new AtomicInteger(0);
        numOfRegistered=new AtomicInteger(0);
        numOfPurchases=new AtomicInteger(0);
    }

    public SystemStats() {

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

    public LocalDate getDate() {
        return date;
    }
}
