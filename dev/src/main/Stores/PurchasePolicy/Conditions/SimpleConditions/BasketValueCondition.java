package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.Condition;

import javax.persistence.Entity;

@Entity
public class BasketValueCondition extends SimpleCondition {
    private double requiredAmount;

    public BasketValueCondition(double requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public BasketValueCondition() {

    }

    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        return requiredAmount <= shoppingBasket.getCleanPrice();
    }
}
