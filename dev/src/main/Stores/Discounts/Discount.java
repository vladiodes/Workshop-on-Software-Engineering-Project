package main.Stores.Discounts;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;

import java.time.LocalDate;

public abstract class Discount {
    private LocalDate until;

    public  Double getPriceFor(Double originalPrice, ShoppingBasket shoppingBasket){
        if (until.isAfter(LocalDate.now()))
            return CalculateDiscount(originalPrice, shoppingBasket);
        else return originalPrice;
    }

    protected abstract Double CalculateDiscount(Double originalPrice, ShoppingBasket shoppingBasket);

    public LocalDate getUntil() {
        return until;
    }

    public void setUntil(LocalDate until) {
        this.until = until;
    }

    @Override
    public abstract String toString();
}
