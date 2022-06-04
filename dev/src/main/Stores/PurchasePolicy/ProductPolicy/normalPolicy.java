package main.Stores.PurchasePolicy.ProductPolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.PurchasePolicy.Discounts.Discount;
import main.Stores.Product;
import main.Stores.Store;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import javax.persistence.Entity;
import javax.persistence.OneToOne;


public class normalPolicy extends DirectPolicy {

    private Discount discount;
    private Double originalPrice;

    private final Store sellingStore;
    public normalPolicy(Double price, Store store) {
        originalPrice = price;
        this.sellingStore = store;
    }

    public normalPolicy() {
        sellingStore=new Store();
    }


    @Override
    public boolean isPurchasable(Product product, Double costumePrice, int amount, User user) {
        return false;
    }

    @Override
    public boolean isPurchasable(Product product, int amount) {
        return product.getQuantity() >= amount;
    }

    @Override
    public boolean productPurchased(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment) {
        product.subtractQuantity(amount);
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean deliveredImmediately(User userToDeliver) {
        return true;
    }

    @Override
    public double getCurrentPrice(User user) {
        if (discount == null)
            return this.originalPrice;
        return discount.getPriceFor(this.getOriginalPrice(), user.getCart().getBasket(sellingStore.getName()));
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

    @Override
    public boolean isAddableToBasket() {
        return true;
    }

}
