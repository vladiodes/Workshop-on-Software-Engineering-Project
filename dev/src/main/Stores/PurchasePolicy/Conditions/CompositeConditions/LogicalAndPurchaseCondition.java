package main.Stores.PurchasePolicy.Conditions.CompositeConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.PurchaseCondition;

import javax.persistence.Entity;

@Entity
public class LogicalAndPurchaseCondition extends CompositePurchaseCondition {
    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        boolean output = true;
        for (PurchaseCondition cond : this.purchaseConditions)
            output &= cond.pass(shoppingBasket);
        return output;
    }
}
