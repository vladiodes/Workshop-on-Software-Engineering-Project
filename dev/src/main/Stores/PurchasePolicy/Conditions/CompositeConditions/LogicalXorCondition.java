package main.Stores.PurchasePolicy.Conditions.CompositeConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.Condition;

import javax.persistence.Entity;

@Entity
public class LogicalXorCondition extends CompositeCondition{

    @Override
    public void addCondition(Condition condition) {
        if(Conditions.size() < 2)
            Conditions.add(condition);
        else throw new IllegalArgumentException("XOR is between 2 conditions.");
    }
    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        return Conditions.get(0).pass(shoppingBasket) ^ Conditions.get(1).pass(shoppingBasket);
    }
}
