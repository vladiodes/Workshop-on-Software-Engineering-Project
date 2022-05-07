package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Stores.Discounts.Discount;
import main.Stores.IStore;
import main.Stores.Product;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

public class normalPolicy extends DirectPolicy {
    private Discount discount;
    private Double originalPrice;
    private final IStore sellingStore;
    public normalPolicy(Double price, IStore store) {
        originalPrice = price;
        this.sellingStore = store;
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

    @Override
    public double getCurrentPrice(ShoppingBasket basket) {
        if (discount == null)
            return this.originalPrice;
        return discount.getPriceFor(this.getOriginalPrice(), basket);
    }

    @Override
    public double getOriginalPrice() {
        return this.originalPrice;
    }

    @Override
    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @Override
    public Discount getDiscount() {
        return this.discount;
    }

    @Override
    public void setOriginalPrice(Double price) {
        this.originalPrice = price;
    }

}