package main.Stores.PurchasePolicy.Conditions.CompositeConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.Condition;
import main.Stores.PurchasePolicy.Discounts.Discount;

import javax.persistence.Entity;

@Entity
public class LogicalAndCondition extends CompositeCondition{
    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        boolean output = true;
        for (Condition cond : this.Conditions)
            output &= cond.pass(shoppingBasket);
        return output;
    }
}
