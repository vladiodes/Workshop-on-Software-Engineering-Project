package main.Stores.PurchasePolicy.Discounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Discounts.Discount;

import java.time.LocalDate;

public class SecretDiscount extends SingleDiscount {
    private final String password;

    public SecretDiscount(LocalDate until, Double percent, String password) {
        super(until, percent);
        this.password = password;
    }


    @Override
    protected Double getPercent(ShoppingBasket shoppingBasket) {
        if (isEligible(shoppingBasket))
            return this.percent;
        else return 0.0;
    }

    @Override
    public boolean isEligible(ShoppingBasket shoppingBasket) {
        return shoppingBasket.hasDiscountPassword(this.password);
    }

    @Override
    public String toString(){
        return super.toString() + " available with secret code only.";
    }
}
