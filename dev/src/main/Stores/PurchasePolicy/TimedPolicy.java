package main.Stores.PurchasePolicy;

import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Stores.Product;
import main.Users.User;
import main.utils.SupplyingInformation;

public abstract  class TimedPolicy implements Policy{
    @Override
    public  abstract boolean isPurchasable (Product product, Double costumePrice);

    @Override
    public  boolean purchase (Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus){
        throw new IllegalArgumentException("Can't purchase event-type policy.");
    }

    @Override
    public abstract boolean bid (Product product, User user, Double costumePrice, NotificationBus bus);
}
