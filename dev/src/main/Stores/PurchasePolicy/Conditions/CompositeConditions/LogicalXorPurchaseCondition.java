package main.Stores.PurchasePolicy.Conditions.CompositeConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.PurchaseCondition;

import javax.persistence.Entity;

@Entity
public class LogicalXorPurchaseCondition extends CompositePurchaseCondition {

    @Override
    public void addCondition(PurchaseCondition purchaseCondition) {
        if(purchaseConditions.size() < 2)
            purchaseConditions.add(purchaseCondition);
        else throw new IllegalArgumentException("XOR is between 2 conditions.");
    }
    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        return purchaseConditions.get(0).pass(shoppingBasket) ^ purchaseConditions.get(1).pass(shoppingBasket);
    }
}
