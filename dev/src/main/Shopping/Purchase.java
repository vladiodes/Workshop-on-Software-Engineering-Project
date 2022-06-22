package main.Shopping;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Logger.Logger;
import main.Stores.Product;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Purchase {
    private final PaymentInformation pinfo;
    private final SupplyingInformation sinfo;
    private final User user;
    private final ShoppingCart cart;
    private final IPayment paymentSystem;
    private final ISupplying supplyingSystem;

    public Purchase(PaymentInformation pinfo, SupplyingInformation sinfo, User user, ShoppingCart cart, IPayment paymentSystem, ISupplying supplyingSystem) {
        this.pinfo = pinfo;
        this.sinfo = sinfo;
        this.user = user;
        this.cart = cart;
        this.paymentSystem = paymentSystem;
        this.supplyingSystem = supplyingSystem;
    }

    public Purchase(Bid bid, ShoppingCart cart,IPayment payment,ISupplying supplying){
        this.pinfo = bid.getPaymentInformation();
        this.sinfo = bid.getSupplyingInformation();
        this.user = bid.getUser();
        this.cart = cart;
        this.paymentSystem = payment;
        this.supplyingSystem = supplying;
    }

    public void executePurchase() throws Exception {
        this.cart.ValidateCart(this.user); //Throws specific exceptions for why cart is not purchasable
        Map<Product, Integer> toDeliver = this.cart.getProductsForPurchase(this.user);
        if (!(paymentSystem.makePayment(pinfo, this.cart.getPrice()) && (toDeliver.size() == 0 || supplyingSystem.supply(sinfo, toDeliver))))
        {
            if(pinfo.getTransactionId()!=0)
            {
                paymentSystem.abort(pinfo);
                if(sinfo.getTransactionId()!=0)
                    supplyingSystem.abort(sinfo);
                throw new Exception("Unexpected purchase error, aborting payment.");
            }

            if(sinfo.getTransactionId()!=0)
            {
                supplyingSystem.abort(sinfo);
                throw new Exception("Unexpected purchase error, aborting supply.");
            }
            throw new Exception("Payment failed");
        }
        try {
            updateMarket();
        }
        catch (Exception e){
            Logger.getInstance().logBug("Purchase",String.format( "Updating market on purchase failed: %s", e.getMessage()));
            if(pinfo.getTransactionId()!=0)
                paymentSystem.abort(pinfo);
            if(sinfo.getTransactionId()!=0)
                supplyingSystem.abort(sinfo);
            throw e;
        }
        Logger.getInstance().logEvent("Purchase", String.format("User %s executed a purchase.", user.getUserName()));
    }

    private void updateMarket() {
        Map<String, ShoppingBasket> baskets = cart.getBaskets();
        for(ShoppingBasket sb : baskets.values())
            sb.purchaseBasket(this.user, supplyingSystem, this.sinfo , this.pinfo, this.paymentSystem);
        this.user.addCartToHistory(this.cart);
    }
}
