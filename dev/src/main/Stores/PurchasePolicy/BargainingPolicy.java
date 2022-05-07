package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

public class BargainingPolicy extends TimedPolicy{

    //TODO
    @Override
    public boolean purchase(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus, PaymentInformation paymentInformation, IPayment payment) {
        return false;
    }

    @Override
    public boolean bid(Bid bid) {
        return false;
    }

    @Override
    public void close(NotificationBus bus) {

    }

    @Override
    public double getCurrentPrice(ShoppingBasket basket) {
        return 0;
    }

    @Override
    public double getOriginalPrice() {
        return 0;
    }

    @Override
    public void setOriginalPrice(Double price) {

    }
}
