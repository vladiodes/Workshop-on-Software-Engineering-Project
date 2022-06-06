package main.Stores.PurchasePolicy.Conditions.CompositeConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.Condition;
import main.Stores.PurchasePolicy.Discounts.Discount;

import javax.persistence.Entity;

@Entity
public class LogicalOrCondition extends CompositeCondition{
    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        boolean output = false;
        for (Condition cond: this.Conditions)
            output |= cond.pass(shoppingBasket);
        return output;
    }
}
