package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.Condition;

public class BasketValueCondition extends SimpleCondition {
    private double requiredAmount;

    public BasketValueCondition(double requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        return requiredAmount <= shoppingBasket.getPrice();
    }
}
