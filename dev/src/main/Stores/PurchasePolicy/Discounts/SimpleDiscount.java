package main.Stores.PurchasePolicy.Discounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Discounts.Discount;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SimpleDiscount extends SingleDiscount {


    public SimpleDiscount(LocalDate until, Double percent) {
        super(until, percent);
    }

    @Override
    public String toString(){
        String formattedDate = this.getUntil().format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
        return String.format("%.0f %% off, ends on %s!", getPercent() * 100, formattedDate);
    }

    @Override
    protected Double getPercent(ShoppingBasket shoppingBasket) {
        return this.percent;
    }

    @Override
    public boolean isEligible(ShoppingBasket shoppingBasket) {
        return true;
    }
}
