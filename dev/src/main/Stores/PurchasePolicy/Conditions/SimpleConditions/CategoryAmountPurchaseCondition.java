package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;

import javax.persistence.Entity;
import java.util.Map;

@Entity
public class CategoryAmountPurchaseCondition extends SimplePurchaseCondition {
    private String category;
    private int requiredAmount;

    public CategoryAmountPurchaseCondition(String category, int amount) {
        this.category = category;
        this.requiredAmount = amount;
    }

    public CategoryAmountPurchaseCondition() {

    }

    @Override
    public boolean pass(ShoppingBasket shoppingBasket) {
        int counter = 0;
        for(Map.Entry<Product,Integer> entry : shoppingBasket.getProductsAndQuantities().entrySet()){
            Product p = entry.getKey();
            Integer amount = entry.getValue();
            if(p.getCategory().equals(this.category))
                counter += amount;
        }
        return counter >= this.requiredAmount;
    }
}
