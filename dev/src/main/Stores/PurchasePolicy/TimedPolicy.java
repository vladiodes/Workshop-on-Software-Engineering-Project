package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Shopping.Purchase;
import main.Shopping.ShoppingCart;
import main.Stores.Discounts.Discount;
import main.Stores.IStore;
import main.Stores.Product;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

public abstract  class TimedPolicy implements Policy{

    @Override
    public abstract boolean purchase(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus, PaymentInformation paymentInformation, IPayment payment);

    @Override
    public boolean isPurchasable(Product product, Double costumePrice, int amount) {
        return false;
    }

    @Override
    public boolean isPurchasable(Product product, int amount) {
        return false;
    }

    @Override
    public void setDiscount(Discount discount){
        throw new IllegalArgumentException("Can't set discount on bargained item.");
    }
    @Override
    public Discount getDiscount(){
        return null;
    }

    protected void purchaseBid(IStore store, Bid bid, String productName, NotificationBus bus) throws Exception {
        ShoppingCart tempCart = new ShoppingCart();
        tempCart.addProductToCart(store, productName, 1); //purchased 1 at a time.
        Purchase temp = new Purchase(bid, tempCart);
        temp.executePurchase(bus);
    }

    @Override
    public boolean deliveredImmediately() {
        return false;
    }
}
