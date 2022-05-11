package main.Stores.SingleProductDiscounts;

import main.Shopping.ShoppingBasket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DirectDiscount extends Discount{
    private final Double percent;

    public DirectDiscount(Double percent, LocalDate until) {
        if(percent >= 1 || percent < 0)
            throw new IllegalArgumentException("Illegal discount percentage.");
        this.setUntil(until);
        this.percent = percent;
    }

    @Override
    protected Double CalculateDiscount(Double originalPrice, ShoppingBasket shoppingBasket) {
        return originalPrice * (1 - this.getPercent());
    }

    public Double getPercent() {
        return percent;
    }

    @Override
    public String toString(){
        String formattedDate = this.getUntil().format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
        return String.format("%.0f %% off, ends on %s!", getPercent() * 100, formattedDate);
    }
}
