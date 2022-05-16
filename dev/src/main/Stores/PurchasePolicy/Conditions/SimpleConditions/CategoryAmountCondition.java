package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Stores.PurchasePolicy.Conditions.Condition;

import java.time.LocalDate;
import java.util.Map;

public class CategoryAmountCondition extends SimpleCondition {
    private String category;
    private int requiredAmount;

    public CategoryAmountCondition(String category, int amount) {
        this.category = category;
        this.requiredAmount = amount;
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
