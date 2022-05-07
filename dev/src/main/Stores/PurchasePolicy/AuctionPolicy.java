package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Logger.Logger;
import main.NotificationBus;
import main.Shopping.Purchase;
import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;
import main.Stores.IStore;
import main.Stores.Product;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionPolicy extends TimedPolicy {
    private final LocalDate  until;
    private Double originalPrice;
    private Bid highestBid;
    private Bid winningBid;
    private NotificationBus bus;
    private  final Timer timer;
    private final IStore sellingStore;
    public AuctionPolicy(LocalDate until, Double originalPrice, NotificationBus bus, IStore sellingStore, String prouctName) {
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
                    winningBid = highestBid;
                    try {
                        purchaseBid(sellingStore, highestBid, prouctName, bus);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    @Override
    public double getCurrentPrice(ShoppingBasket basket) {
        return highestBid.getCostumePrice();
    }

    @Override
    public double getOriginalPrice() {
        return this.originalPrice;
    }

    public LocalDate getUntil() {
        return until;
    }

    @Override
    public void setOriginalPrice(Double price) {
        this.originalPrice = price;
    }

    @Override
    public boolean purchase(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus, PaymentInformation paymentInformation, IPayment payment) {
        if(winningBid.getUser() == user){
            product.subtractQuantity(1);
            return true;
        }
        Logger.getInstance().logBug("AuctionPolicy", "Attempt to purchase auction before its over.");
        throw  new IllegalArgumentException("can't buy yet");
    }
}
