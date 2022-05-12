package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
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
    public abstract boolean productPurchased(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment);

    @Override
    public void setDiscount(Discount discount){
        throw new IllegalArgumentException("Can't set discount on bargained item.");
    }
    @Override
    public Discount getDiscount(){
        return null;
    }

    protected void purchaseBid(IStore store, Bid bid) throws Exception {
        ShoppingCart tempCart = new ShoppingCart();
        tempCart.addProductToCart(store, bid.getProduct().getName(), 1); //purchased 1 at a time.
        Purchase temp = new Purchase(bid, tempCart);
        temp.executePurchase();
    }

    @Override
    public boolean deliveredImmediately(User userToDeliver) {
        return false;
    }
}
