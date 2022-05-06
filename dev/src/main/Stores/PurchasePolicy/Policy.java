package main.Stores.PurchasePolicy;

import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Stores.Product;
import main.Users.User;
import main.utils.SupplyingInformation;

public interface Policy {
    /***
     * @return if its possible to buy this at this price according to the policy.
     */
    public  boolean isPurchasable (Product product, Double costumePrice);
    public  boolean isPurchasable(Product product, int amount);

    /***
     * assumes payment was successful and updates values accordingly.
     * @return true if product quantity is was updated.
     */
    public  boolean purchase (Product product, User user, Double costumePrice,int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus);

    /***
     * @return used by purchase policies that allow bargaining.
     */
    public boolean bid (Product product, User user, Double costumePrice, NotificationBus bus);
}