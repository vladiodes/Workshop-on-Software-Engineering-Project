package main.Stores.PurchasePolicy.ProductPolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.Product;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.List;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract  class DirectPolicy extends Policy{
    @Override
    public abstract boolean productPurchased(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment);
    @Override
    public boolean bid (Bid bid) {
        throw new IllegalArgumentException("This product is not up for bidding.");
    }
    @Override
    public List<Bid> getBids(User requestingUser){
        return null;
    }
    @Override
    public void approveBid(User user, User approvingUser,IPayment payment,ISupplying supplying){
        throw new IllegalArgumentException("No bidding on this product.");
    }
    @Override
    public void declineBid(User user){
        throw new IllegalArgumentException("No bidding on this product.");
    }
    @Override
    public void counterOfferBid(User user, Double offer){
        throw new IllegalArgumentException("No bidding on this product.");
    }
}
