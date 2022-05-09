package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Stores.Product;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import java.util.List;

public abstract  class DirectPolicy implements Policy{
    @Override
    public abstract boolean productPurchased(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus, PaymentInformation paymentInformation, IPayment payment);
    @Override
    public boolean bid (Bid bid) {
        throw new IllegalArgumentException("This product is not up for bidding.");
    }
    @Override
    public List<Bid> getBids(){
        return null;
    }
    @Override
    public void approveBid(String username, User approvingUser, NotificationBus bus){
        throw new IllegalArgumentException("No bidding on this product.");
    }
    @Override
    public void declineBid(String username, NotificationBus bus){
        throw new IllegalArgumentException("No bidding on this product.");
    }
    @Override
    public void counterOfferBid(String username, Double offer, NotificationBus bus){
        throw new IllegalArgumentException("No bidding on this product.");
    }
}
