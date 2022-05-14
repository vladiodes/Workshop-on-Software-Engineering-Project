package main.Stores.PurchasePolicy.Discounts.SimpleDiscounts;

import main.Shopping.ShoppingBasket;

import java.time.LocalDate;

public class SecretDiscount extends SingleDiscount {
    private final String password;

    public SecretDiscount(LocalDate until, Double percent, String password) {
        super(until, percent);
        this.password = password;
    }


    @Override
    public Double getPercent(ShoppingBasket shoppingBasket) {
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
