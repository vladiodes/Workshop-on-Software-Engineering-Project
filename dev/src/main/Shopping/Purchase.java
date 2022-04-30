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

    public void executePurchase(NotificationBus bus) throws Exception {
        if (!this.cart.ValidateCart())
            throw new Exception("Cart is unpurchasable.");
        if (!paymentSystem.validateCard(pinfo))
            throw new Exception("Payment authentication failed.");
        if (!supplyingSystem.bookDelivery(sinfo))
            throw new Exception("Supplier authentication failed");
        if (!(paymentSystem.makePayment(pinfo, this.cart.getPrice()) && supplyingSystem.supply(sinfo, this.cart.getProducts())))
        {
            paymentSystem.abort(pinfo);
            supplyingSystem.abort(sinfo);
            throw new Exception("Unexpected purchase error, aborting.");
        }
        updateMarket(bus);
        Logger.getInstance().logEvent("Purchase", String.format("User %s executed a purchase.", user.getUserName()));
    }

    private void updateMarket(NotificationBus bus) {
        this.user.resetCart();
        ConcurrentHashMap<String, ShoppingBasket> baskets = cart.getBaskets();
        for(ShoppingBasket sb : baskets.values())
            sb.purchaseBasket(bus);
        this.user.addCartToHistory(this.cart);
    }
}
