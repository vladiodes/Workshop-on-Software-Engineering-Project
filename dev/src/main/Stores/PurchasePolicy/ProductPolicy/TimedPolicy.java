package main.Stores.PurchasePolicy.ProductPolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Shopping.Purchase;
import main.Shopping.ShoppingCart;
import main.Stores.PurchasePolicy.Discounts.Discount;
import main.Stores.Product;
import main.Stores.Store;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract  class TimedPolicy extends Policy{

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

    protected void purchaseBid(Store store, Bid bid,IPayment payment,ISupplying supplying) throws Exception {
        ShoppingCart tempCart = new ShoppingCart(bid.getUser());
        tempCart.addProductToCart(store, bid.getProduct().getName(), 1); //purchased 1 at a time.
        Purchase temp = new Purchase(bid, tempCart,payment,supplying);
        temp.executePurchase();
    }

    @Override
    public boolean deliveredImmediately(User userToDeliver) {
        return false;
    }
}
