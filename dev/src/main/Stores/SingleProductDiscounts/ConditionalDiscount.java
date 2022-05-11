package main.Stores.SingleProductDiscounts;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.utils.Restriction;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ConditionalDiscount extends Discount{
    private final Restriction restriction;
    private final double percent;

    public ConditionalDiscount(Restriction restrictions,Double percent ,LocalDate until) {
        this.setUntil(until);
        this.restriction = restrictions;
        this.percent = percent;
    }

    @Override
    protected Double CalculateDiscount(Double originalPrice, ShoppingBasket shoppingBasket) {
        if(isEligible(shoppingBasket))
            return originalPrice * (1 - percent);
        return originalPrice;
    }

    private String restrictionToString(Restriction rest, Double discount){
        String output = "If you buy: ";;
        for (Map.Entry<Product, Integer> ent: restriction.entrySet())
            output= output + ent.getValue() +"x " +ent.getKey().getName() +", ";
        return output + String.format("you get %.0f %% off!", discount * 100);
    }

    @Override
    public String toString() {
        return restrictionToString(this.restriction, this.percent);
    }

    @Override
    public boolean isEligible(ShoppingBasket shoppingBasket) {
        if (shoppingBasket == null)
            return false;
        for(Map.Entry<Product,Integer> entry: this.restriction.entrySet())
            if(!shoppingBasket.hasAmount(entry.getKey(), entry.getValue()))
                return false;
        return true;
    }
}
