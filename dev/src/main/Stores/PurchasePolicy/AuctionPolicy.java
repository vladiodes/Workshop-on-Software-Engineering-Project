package main.Stores.PurchasePolicy;

import main.NotificationBus;
import main.Shopping.Purchase;
import main.Stores.IStore;
import main.utils.Bid;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionPolicy extends TimedPolicy {
    private final LocalDate  until;
    private Double originalPrice;
    private Bid highestBid;
    private NotificationBus bus;
    private  final Timer timer;
    private final IStore sellingStore;
    public AuctionPolicy(LocalDate until, Double originalPrice, NotificationBus bus, IStore sellingStore) {
        this.sellingStore = sellingStore;
        this.bus = bus;
        this.until = until;
        this.originalPrice = originalPrice;
        this.highestBid = null;
        this.timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(highestBid == null)
                    bus.addMessage(sellingStore, "System notification","Product wasn't sold, no valid bid was submitted.");
                else {

                }
            }
        }, Date.from(until.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public synchronized boolean bid(Bid bid) {
        if (LocalDate.now().isAfter(until))
            throw new IllegalArgumentException("Auction for this product is past due.");
        if(highestBid == null && bid.getCostumePrice() >= originalPrice){
            highestBid = bid;
            return true;
        } else if (highestBid.compareTo(bid) < 0) {
            highestBid = bid;
            return true;
        }
        return false;
    }

    @Override
    public void close(NotificationBus bus) {
        bus.addMessage(highestBid.getUser(), "Auction was closed, no winner declared.");
    }

    public LocalDate getUntil() {
        return until;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public Bid getHighestBid() {
        return highestBid;
    }
}
