package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Stores.PurchasePolicy.Conditions.Condition;

import java.time.LocalDate;

public class ProductAmountCondition extends SimpleCondition {
    private Product product;
    private int requiredAmount;

    public ProductAmountCondition(int requiredAmount, Product product) {
        this.requiredAmount = requiredAmount;
        this.product = product;
    }

    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        return shoppingBasket.hasAmount(product, requiredAmount);
    }
}
