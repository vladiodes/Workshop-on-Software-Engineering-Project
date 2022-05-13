package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.SingleProductDiscounts.Discount;
import main.Stores.Product;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;
import main.utils.Bid;

import java.util.List;

public interface Policy {
    /***
     * @return if its possible to buy this at this costume price according to the policy.
     */
    public boolean isPurchasable(Product product, Double costumePrice, int amount, User user);

    /**
     * @return if its possible to buy this amount of that product
     */
    public boolean isPurchasable(Product product, int amount);

    /***
     * assumes payment was successful and updates values accordingly.
     * timed policies doesn't allow purchase.
     * @return true if product quantity is was updated.
     */
    public boolean productPurchased(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment);

    /***
     * used to bid on a product, used for timed policies.
     * @param bid to place.
     * @return true if should notify store staff.
     */
    public boolean bid(Bid bid);
    public List<Bid> getBids();
    public void approveBid(User user, User approvingUser) throws Exception;
    public void declineBid(User user);
    public void counterOfferBid(User user, Double offer);

    /***
     *when changing policies we want to end the previous one gracefully (refunding if raffle for instance)
     */
    public void close();

    /***
     * used to decide if the product should be delivered immediately for a specific user.
     */
    public boolean deliveredImmediately(User userToDeliver);

    /***
     * returns the current price.
     * @param user
     */
    public double getCurrentPrice(User user);

    /***
     * get original price
     */
    public double getOriginalPrice();
    public void setDiscount(Discount discount);
    public Discount getDiscount();
    public void setOriginalPrice(Double price);

}