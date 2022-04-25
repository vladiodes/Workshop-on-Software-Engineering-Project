package main.Shopping;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Payment.PaymentAdapter;
import main.ExternalServices.Supplying.ISupplying;
import main.ExternalServices.Supplying.SupplyingAdapter;
import main.Logger.Logger;
import main.NotificationBus;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import java.util.concurrent.ConcurrentHashMap;

public class Purchase {
    private PaymentInformation pinfo;
    private SupplyingInformation sinfo;
    private User user;
    private ShoppingCart cart;

    public Purchase(PaymentInformation pinfo, SupplyingInformation sinfo, User user, ShoppingCart cart) {
        this.pinfo = pinfo;
        this.sinfo = sinfo;
        this.user = user;
        this.cart = cart;
    }

    public void executePurchase(NotificationBus bus) throws Exception {
        IPayment payment = new PaymentAdapter();
        ISupplying supplier = new SupplyingAdapter();
        if (!this.cart.ValidateCart())
            throw new Exception("Cart is unpurchasable.");
        if (!payment.validateCard(pinfo))
            throw new Exception("Payment authentication failed.");
        if (!supplier.bookDelivery(sinfo))
            throw new Exception("Supplier authentication failed");
        if (!(payment.makePayment(pinfo, this.cart.getPrice()) && supplier.supply(sinfo, this.cart.getProducts())))
        {
            payment.abort(pinfo);
            supplier.abort(sinfo);
            throw new Exception("Unexpected purchase error, aborting.");
        }
        this.user.resetCart();
        ConcurrentHashMap<String, ShoppingBasket> baskets = cart.getBaskets();
        for(ShoppingBasket sb : baskets.values())
            sb.purchaseBasket(bus);
        this.user.addCartToHistory(this.cart);
        Logger.getInstance().logEvent("Purchase", String.format("User %s executed a purchase.", user.getUserName()));
    }
}
