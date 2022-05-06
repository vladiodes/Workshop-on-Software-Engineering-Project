package main.Stores.PurchasePolicy;

import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Stores.Product;
import main.Users.User;
import main.utils.SupplyingInformation;

public class normalPolicy extends DirectPolicy {
    public normalPolicy() {
    }


    @Override
    public boolean isPurchasable(Product product, Double costumePrice) {
        return costumePrice == null;
    }

    @Override
    public boolean purchase(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus) {
        product.subtractQuantity(amount);
        return true;
    }
}
