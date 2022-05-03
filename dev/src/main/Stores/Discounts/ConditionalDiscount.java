package main.Stores.Discounts;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.utils.Pair;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionalDiscount extends Discount{
    private HashMap<HashMap<Product, Integer>, Double> restrictions;

    public ConditionalDiscount(HashMap<HashMap<Product, Integer>, Double> restrictions, LocalDate until) {
        this.setUntil(until);
        this.restrictions = restrictions;
    }

    private boolean restrictionMet(HashMap<Product, Integer> rest, ShoppingBasket shoppingBasket){
        for(Map.Entry<Product,Integer> entry: rest.entrySet())
            if(!shoppingBasket.hasAmount(entry.getKey(), entry.getValue()))
                return false;
        return true;
    }

    @Override
    protected Double CalculateDiscount(Product product, ShoppingBasket shoppingBasket) {
        double output = product.getCleanPrice();
        for(Map.Entry<HashMap<Product,Integer>, Double> restriction: restrictions.entrySet())
            if(restrictionMet(restriction.getKey(), shoppingBasket)){
                double discountedValue = product.getCleanPrice() * (1- restriction.getValue());
                if(output > discountedValue)
                    output = discountedValue;
            }
        return output;
    }

    private String restrictionToString(Map.Entry<HashMap<Product, Integer>, Double> rest){
        String output = "If you buy: ";
        HashMap<Product, Integer> restriction = rest.getKey();
        Double discount = rest.getValue();
        for (Map.Entry<Product, Integer> ent: restriction.entrySet())
            output= output + ent.getValue() +"x " +ent.getKey().getName() +", ";
        return output + String.format("you get %.0f %% off!", discount * 100);
    }

    @Override
    public String toString() {
        String output = "";
        for (Map.Entry<HashMap<Product,Integer>, Double> restriction: restrictions.entrySet())
            output = output + restrictionToString(restriction) +"\n";
        return output;
    }
}
