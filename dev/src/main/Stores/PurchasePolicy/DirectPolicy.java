package main.Stores.PurchasePolicy;

import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Stores.Product;
import main.Users.User;
import main.utils.SupplyingInformation;

public abstract  class DirectPolicy implements Policy{
    @Override
    public  boolean isPurchasable(Product product, int amount){
        return product.getQuantity() >= amount;
    }
    @Override
    public abstract boolean purchase (Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus);
    @Override
    public boolean bid (Product product, User user, Double costumePrice, NotificationBus bus) {
        throw new IllegalArgumentException("This product is not up for bidding.");
    }
}
