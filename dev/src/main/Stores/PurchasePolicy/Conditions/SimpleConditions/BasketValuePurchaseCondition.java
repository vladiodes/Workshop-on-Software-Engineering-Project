package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Shopping.ShoppingBasket;

import javax.persistence.Entity;

@Entity
public class BasketValuePurchaseCondition extends SimplePurchaseCondition {
    private double requiredAmount;

    public BasketValuePurchaseCondition(double requiredAmount) {
        this.requiredAmount = requiredAmount;
    }

    public BasketValuePurchaseCondition() {

    }

    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        return requiredAmount <= shoppingBasket.getCleanPrice();
    }
}
