package main.Stores.PurchasePolicy.Discounts;

import main.Shopping.ShoppingBasket;

import java.time.LocalDate;

public class PlusCompositeDiscount extends CompositeDiscount{
    public PlusCompositeDiscount(LocalDate until) {
        super(until);
    }

    @Override
    protected Double CalculateDiscount(Double originalPrice, ShoppingBasket basket) {
        return originalPrice * (1 - getPercent(basket));
    }

    @Override
    protected Double getPercent(ShoppingBasket shoppingBasket) {
        Double percent = 0.0;
        for(Discount d: this.discounts)
            if(d.isEligible(shoppingBasket))
                percent += d.getPercent(shoppingBasket);
        return percent;
    }
}
