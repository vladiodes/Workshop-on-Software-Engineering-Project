package main.Stores.PurchasePolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Stores.SingleProductDiscounts.Discount;
import main.Publisher.PersonalNotification;
import main.Stores.Discounts.Discount;
import main.Stores.IStore;
import main.Stores.Product;
import main.Users.User;
import main.utils.Pair;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class rafflePolicy extends DirectPolicy {
    private double accumaltivePrice;
    private ConcurrentHashMap<User, Double> participants;
    private ConcurrentHashMap<User, Pair<ISupplying,SupplyingInformation>> userSupplyInformation;
    private ConcurrentHashMap<User, Pair<IPayment,PaymentInformation>> userPaymentInformation;
    private final IStore store;
    private double originalPrice;
    public rafflePolicy(IStore store, Double originalPrice) {
        this.store = store;
        this.accumaltivePrice = 0;
        this.participants = new ConcurrentHashMap<>();
        userSupplyInformation = new ConcurrentHashMap<>();
        userPaymentInformation = new ConcurrentHashMap<>();
        this.setOriginalPrice(originalPrice);
    }

    /***
     * doesn't work with discounts.
     */
    @Override
    public boolean isPurchasable(Product product, Double costumePrice, int amount, User user){
        return amount * costumePrice + this.accumaltivePrice <= product.getCleanPrice();
    }

    @Override
    public boolean isPurchasable(Product product, int amount) {
        return product.getQuantity() >= 1;
    }


    @Override
    public synchronized boolean productPurchased(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment){
        double userMoney = costumePrice * amount;
        accumaltivePrice += userMoney;
        addUserToRaffle(user, userMoney, supplying, supplyingInformation, payment, paymentInformation);
        if (accumaltivePrice == this.originalPrice){
            executeRaffle(product);
            ResetRaffle();
            product.subtractQuantity(1);
        }
        return true;
    }

    @Override
    public void close() {
        for (Map.Entry<User, Pair<IPayment,PaymentInformation>> entry : userPaymentInformation.entrySet()){
            Pair<IPayment, PaymentInformation> pay = entry.getValue();
            pay.first.abort(pay.second);
            entry.getKey().notifyObserver(new PersonalNotification(store.getName(),"Raffle was closed, you should get refunded according to the payment service policy."));
        }
        this.ResetRaffle();
    }

    @Override
    public boolean deliveredImmediately(User userToDeliver) {
        return false;
    }

    @Override
    public double getCurrentPrice(User user) {
        return this.originalPrice - accumaltivePrice;
    }

    @Override
    public double getOriginalPrice() {
        return this.originalPrice;
    }

    @Override
    public void setDiscount(Discount discount) {
        throw new IllegalArgumentException("Can't set discount for raffle");
    }

    @Override
    public Discount getDiscount() {
        return null;
    }

    @Override
    public void setOriginalPrice(Double price) {
        if (accumaltivePrice >= price)
            throw new IllegalArgumentException("can't set price to be more than gathered so far.");
        else this.originalPrice = price;
    }

    private void ResetRaffle(){
        this.accumaltivePrice = 0;
        this.participants = new ConcurrentHashMap<>();
        userSupplyInformation = new ConcurrentHashMap<>();
        userPaymentInformation = new ConcurrentHashMap<>();
    }

    private void executeRaffle(Product product){
        User winner = evaluateWinner();
        Pair<ISupplying, SupplyingInformation> supl = userSupplyInformation.get(winner);
        Map<Product, Integer> items = new HashMap<>();
        items.put(product, 1);
        if(supl.first.supply(supl.second, items)){
            winner.notifyObserver(new PersonalNotification(store.getName(),String.format("You have won the raffle for %s!", product.getName())));
        } else {
            winner.notifyObserver(new PersonalNotification(store.getName(),
                    String.format("You have won the raffle for %s! however the delivery service failed, please contact a store staff!", product.getName())));
        }
    }

    private User evaluateWinner() {
        Double curr = 0.0;
        double winningNumber = Math.random() * this.originalPrice;
        User winner = null;
        for(Map.Entry<User, Double> entry : participants.entrySet()){
            if(entry.getValue() + curr > winningNumber && curr < winningNumber){
                winner = entry.getKey();
                break;
            } else curr+= entry.getValue();
        }
        return winner;
    }

    private void addUserToRaffle(User user, Double price, ISupplying supplying, SupplyingInformation supplyingInformation, IPayment payment, PaymentInformation paymentInformation){
        double oldvalue;
        if(participants.containsKey(user)){
            oldvalue = participants.get(user);
        } else oldvalue = 0;
        userSupplyInformation.put(user, new Pair<>(supplying, supplyingInformation));
        userPaymentInformation.put(user, new Pair<>(payment, paymentInformation));
        participants.put(user, oldvalue + price);
    }
}
