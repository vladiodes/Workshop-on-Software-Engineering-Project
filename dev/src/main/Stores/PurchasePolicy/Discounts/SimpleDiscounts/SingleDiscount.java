package main.Stores.PurchasePolicy.Discounts.SimpleDiscounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Discounts.Discount;

import java.time.LocalDate;

public abstract class SingleDiscount extends Discount {
    protected Double percent;
    public SingleDiscount(LocalDate until, Double percent) {
        super(until);
        this.percent = percent;
    }

    protected Double getPercent(){
        return this.percent;
    }

    protected Double CalculateDiscount(Double originalPrice, ShoppingBasket basket) {
        return originalPrice * (1 - this.percent);
    }
}
