package main.Stores.PurchasePolicy.Discounts.SimpleDiscounts;

import main.Shopping.ShoppingBasket;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
public class SimpleDiscount extends SingleDiscount {


    public SimpleDiscount(LocalDate until, Double percent) {
        super(until, percent);
    }

    public SimpleDiscount() {
        super();
    }

    @Override
    public String toString(){
        String formattedDate = this.getUntil().format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
        return String.format("%.0f %% off, ends on %s!", getPercent() * 100, formattedDate);
    }

    @Override
    public Double getPercent(ShoppingBasket shoppingBasket) {
        return this.percent;
    }

    @Override
    public boolean isEligible(ShoppingBasket shoppingBasket) {
        return true;
    }
}
