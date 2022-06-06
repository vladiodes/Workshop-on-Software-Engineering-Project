package main.Stores.PurchasePolicy.ProductPolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Logger.Logger;
import main.Publisher.PersonalNotification;
import main.Publisher.StoreNotification;
import main.Stores.Product;
import main.Stores.Store;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AuctionPolicy extends TimedPolicy {
    private final LocalDate  until;
    private Double originalPrice;
    private Bid highestBid;
    private Bid winningBid;;
    private  final Timer timer;
    private final Store sellingStore;
    public AuctionPolicy(LocalDate until, Double originalPrice, Store sellingStore, String prouctName) {
        this.sellingStore = sellingStore;
        this.until = until;
        this.originalPrice = originalPrice;
        this.highestBid = null;
        this.timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(highestBid == null)
                    sellingStore.sendMessageToStaffOfStore(new StoreNotification(sellingStore.getName(),String.format("%s wasn't sold, no valid bid was submitted",prouctName)));
                else {
                    winningBid = highestBid;
                    try {
                        purchaseBid(sellingStore, highestBid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ChronoUnit.DAYS.between(LocalDate.now(), until));
    }

    @Override
    public synchronized boolean bid(Bid bid) {
        if (LocalDate.now().isAfter(until))
            throw new IllegalArgumentException("Auction for this product is past due.");
        if(highestBid == null && bid.getCostumePrice() >= originalPrice){
            highestBid = bid;
            return false;
        } else if (highestBid.getCostumePrice() < bid.getCostumePrice()) {
            highestBid = bid;
            return false;
        }
        throw new IllegalArgumentException("Invalid bidding values.");
    }

    @Override
    public List<Bid> getBids() {
        List<Bid> output = new LinkedList<>();
        output.add(this.highestBid);
        return output;
    }

    @Override
    public void approveBid(User user, User approvingUser) {
        throw new IllegalArgumentException("In auction bids don't need aproval.");
    }

    @Override
    public void declineBid(User user) {
        throw new IllegalArgumentException("In auction bids can't be dismissed.");
    }

    @Override
    public void counterOfferBid(User user, Double offer) {
        throw new IllegalArgumentException("In auction bids can't be countered.");
    }

    @Override
    public void close() {
        highestBid.getUser().notifyObserver(new PersonalNotification(sellingStore.getName(),"Auction was closed, no winner declared."));
        timer.cancel();
    }

    @Override
    public double getCurrentPrice(User user) {
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
    public boolean isAddableToBasket() {
        return false;
    }

    @Override
    public boolean isPurchasable(Product product, Double costumePrice, int amount, User user) {
        return until.isBefore(LocalDate.now()) && user == winningBid.getUser();
    }

    @Override
    public boolean isPurchasable(Product product, int amount) {
        return until.isBefore(LocalDate.now()) && amount == 1;
    }

    @Override
    public boolean productPurchased(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment) {
        if(winningBid.getUser() == user && until.isBefore(LocalDate.now())){
            product.subtractQuantity(1);
            return true;
        }
        Logger.getInstance().logBug("AuctionPolicy", "Attempt to purchase auction before its over.");
        throw  new IllegalArgumentException("can't buy yet");
    }

    @Override
    public boolean deliveredImmediately(User user){
        return until.isBefore(LocalDate.now()) && winningBid.getUser() == user;
    }
}
