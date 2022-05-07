package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Stores.Product;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;
import main.utils.Bid;

public interface Policy {
    /***
     * @return if its possible to buy this at this costume price according to the policy.
     */
    public boolean isPurchasable(Product product, Double costumePrice, int amount);

    /**
     * @return if its possible to buy this amount of that product
     */
    public boolean isPurchasable(Product product, int amount);

    /***
     * assumes payment was successful and updates values accordingly.
     * timed policies doesn't allow purchase.
     * @return true if product quantity is was updated.
     */
    public boolean purchase(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus, PaymentInformation paymentInformation, IPayment payment);

    /***
     * used by purchase policies that allow bargaining.
     */
    public boolean bid(Bid bid);

    /***
     *when changing policies we want to end the previous one gracefully (refunding if raffle for instance)
     */
    public void close(NotificationBus bus);

    /***
     * used to decide if the product should be delivered immediately
     */
    public boolean deliveredImmediately();
}