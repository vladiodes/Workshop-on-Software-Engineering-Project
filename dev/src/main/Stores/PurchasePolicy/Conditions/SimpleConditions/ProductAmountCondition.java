package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Stores.PurchasePolicy.Conditions.Condition;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class ProductAmountCondition extends SimpleCondition {
    @OneToOne
    private Product product;
    private int requiredAmount;

    public ProductAmountCondition(int requiredAmount, Product product) {
        this.requiredAmount = requiredAmount;
        this.product = product;
    }

    public ProductAmountCondition() {

    }

    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        return shoppingBasket.hasAmount(product, requiredAmount);
    }
}
