package main.Stores.PurchasePolicy.ProductPolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.PurchasePolicy.Discounts.Discount;
import main.Stores.Product;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;
import main.utils.Bid;
import java.util.List;
import javax.persistence.*;


public abstract class Policy {


    private int id;

    /***
     * @return if its possible to buy this at this costume price according to the policy.
     */
    public abstract boolean isPurchasable(Product product, Double costumePrice, int amount, User user);

    /**
     * @return if its possible to buy this amount of that product
     */
    public abstract boolean isPurchasable(Product product, int amount);

    /***
     * assumes payment was successful and updates values accordingly.
     * timed policies doesn't allow purchase.
     * @return true if product quantity is was updated.
     */
    public abstract boolean productPurchased(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment);

    /***
     * used to bid on a product, used for timed policies.
     * @param bid to place.
     * @return true if should notify store staff.
     */
    public abstract boolean bid(Bid bid);
    public abstract List<Bid> getBids();
    public abstract void approveBid(User user, User approvingUser) throws Exception;
    public abstract void declineBid(User user);
    public abstract void counterOfferBid(User user, Double offer);

    /***
     *when changing policies we want to end the previous one gracefully (refunding if raffle for instance)
     */
    public abstract void close();

    /***
     * used to decide if the product should be delivered immediately for a specific user.
     */
    public abstract boolean deliveredImmediately(User userToDeliver);

    /***
     * returns the current price.
     * @param user
     */
    public abstract double getCurrentPrice(User user);

    /***
     * get original price
     */
    public abstract double getOriginalPrice();
    public abstract void setDiscount(Discount discount);
    public abstract Discount getDiscount();
    public abstract void setOriginalPrice(Double price);

    public abstract boolean isAddableToBasket();
}