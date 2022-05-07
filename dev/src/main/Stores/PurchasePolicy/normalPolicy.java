package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Stores.Product;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

public class normalPolicy extends DirectPolicy {
    public normalPolicy() {
    }


    @Override
    public boolean isPurchasable(Product product, Double costumePrice, int amount) {
        return false;
    }

    @Override
    public boolean isPurchasable(Product product, int amount) {
        return product.getQuantity() >= amount;
    }

    @Override
    public boolean purchase(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus, PaymentInformation paymentInformation, IPayment payment) {
        product.subtractQuantity(amount);
        return true;
    }

    @Override
    public void close(NotificationBus bus) {

    }

    @Override
    public boolean deliveredImmediately() {
        return true;
    }

}
