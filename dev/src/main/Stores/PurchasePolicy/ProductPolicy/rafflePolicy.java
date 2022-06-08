package main.Stores.PurchasePolicy.ProductPolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.PurchasePolicy.Discounts.Discount;
import main.Publisher.PersonalNotification;
import main.Stores.Product;
import main.Stores.Store;
import main.Users.User;
import main.utils.Pair;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Entity
public class rafflePolicy extends DirectPolicy {
    private double accumaltivePrice;
    @Transient
    private ISupplying supplying;
    @Transient
    private IPayment payment;
    @ElementCollection
    private Map<User, Double> participants;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_supplying_info_tbl",
            joinColumns = {@JoinColumn(name = "policy_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "supplying_info_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "user_id")
    private Map<User, SupplyingInformation> userSupplyInformation;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_payment_info_tbl",
            joinColumns = {@JoinColumn(name = "policy_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "payment_info_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "user_id")
    private Map<User, PaymentInformation> userPaymentInformation;
    @OneToOne
    private final Store store;
    private double originalPrice;
    public rafflePolicy(Store store, Double originalPrice) {
        this.store = store;
        this.accumaltivePrice = 0;
        this.participants = Collections.synchronizedMap(new HashMap<>());
        userSupplyInformation = Collections.synchronizedMap(new HashMap<>());
        userPaymentInformation = Collections.synchronizedMap(new HashMap<>());
        this.setOriginalPrice(originalPrice);
    }

    public rafflePolicy() {
        store=new Store();
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
        for (Map.Entry<User,PaymentInformation> entry : userPaymentInformation.entrySet()){
            try
            {
                payment.abort(entry.getValue());
                entry.getKey().notifyObserver(new PersonalNotification(store.getName(),"Raffle was closed, you should get refunded according to the payment service policy."));
            }
            catch (Exception e)
            {
                entry.getKey().notifyObserver(new PersonalNotification(store.getName(),"Raffle was closed, but we failed to refund you, please contact an admin for further help"));
            }
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

    @Override
    public boolean isAddableToBasket() {
        return true;
    }

    private void ResetRaffle(){
        this.accumaltivePrice = 0;
        this.participants = new ConcurrentHashMap<>();
        userSupplyInformation = new ConcurrentHashMap<>();
        userPaymentInformation = new ConcurrentHashMap<>();
    }

    private void executeRaffle(Product product){
        User winner = evaluateWinner();
        SupplyingInformation supl = userSupplyInformation.get(winner);
        Map<Product, Integer> items = new HashMap<>();
        items.put(product, 1);
        if(supplying.supply(supl, items)){
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
        if(this.supplying==null) {
            this.supplying = supplying;
            this.payment = payment;
        }
        if(participants.containsKey(user)){
            oldvalue = participants.get(user);
        } else oldvalue = 0;
        userSupplyInformation.put(user, supplyingInformation);
        userPaymentInformation.put(user, paymentInformation);
        participants.put(user, oldvalue + price);
    }
}
