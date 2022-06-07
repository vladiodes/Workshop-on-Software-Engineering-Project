package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class ProductAmountPurchaseCondition extends SimplePurchaseCondition {
    @OneToOne
    private Product product;
    private int requiredAmount;

    public ProductAmountPurchaseCondition(int requiredAmount, Product product) {
        this.requiredAmount = requiredAmount;
        this.product = product;
    }

    public ProductAmountPurchaseCondition() {

    }

    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        return shoppingBasket.hasAmount(product, requiredAmount);
    }
}
