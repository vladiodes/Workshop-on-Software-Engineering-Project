package main.Stores.PurchasePolicy;

import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Stores.Product;
import main.Users.User;
import main.utils.Pair;
import main.utils.SupplyingInformation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class rafflePolicy extends DirectPolicy {
    private double accumaltivePrice;
    private ConcurrentHashMap<User, Double> participants;
    private ConcurrentHashMap<User, Pair<ISupplying,SupplyingInformation>> userSupplyInformation;

    public rafflePolicy() {
        this.accumaltivePrice = 0;
        this.participants = new ConcurrentHashMap<>();
        userSupplyInformation = new ConcurrentHashMap<>();
    }

    /***
     * doesn't work with discounts.
     */
    @Override
    public boolean isPurchasable(Product product,Double costumePrice){
        return costumePrice + this.accumaltivePrice <= product.getCleanPrice();
    }


    @Override
    public synchronized boolean  purchase(Product product, User user, Double costumePrice, int amount ,ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus){
        double userMoney = costumePrice * amount;
        accumaltivePrice += userMoney;
        addUserToRaffle(user, userMoney, supplying, supplyingInformation);
        if (accumaltivePrice == product.getCleanPrice()){
            executeRaffle(product, bus);
            ResetRaffle();
            product.subtractQuantity(1);
        }
        return true;
    }

    private void ResetRaffle(){
        this.accumaltivePrice = 0;
        this.participants = new ConcurrentHashMap<>();
        userSupplyInformation = new ConcurrentHashMap<>();
    }

    private void executeRaffle(Product product, NotificationBus bus){
        User winner = evaluateWinner(product);
        Pair<ISupplying, SupplyingInformation> supl = userSupplyInformation.get(winner);
        Map<Product, Integer> items = new HashMap<>();
        items.put(product, 1);
        if(supl.first.supply(supl.second, items)){
            bus.addMessage(winner, String.format("You have won the raffle for %s!", product.getName()));
        } else {
            bus.addMessage(winner, String.format("You have won the raffle for %s! however the delivery service failed, please contact a store staff!", product.getName()));
        }
    }

    private User evaluateWinner(Product product) {
        Double curr = 0.0;
        double winningNumber = Math.random() * product.getCleanPrice();
        User winner = null;
        for(Map.Entry<User, Double> entry : participants.entrySet()){
            if(entry.getValue() + curr > winningNumber && curr < winningNumber){
                winner = entry.getKey();
                break;
            } else curr+= entry.getValue();
        }
        return winner;
    }

    private void addUserToRaffle(User user, Double price, ISupplying supplying, SupplyingInformation supplyingInformation){
        double oldvalue;
        if(participants.containsKey(user)){
            oldvalue = participants.get(user);
        } else oldvalue = 0;
        userSupplyInformation.put(user, new Pair<>(supplying, supplyingInformation));
        participants.put(user, oldvalue + price);
    }
}
