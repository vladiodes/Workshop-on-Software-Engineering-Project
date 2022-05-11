package main.Stores.SingleProductDiscounts;

import main.Shopping.ShoppingBasket;

import java.time.LocalDate;
import java.util.List;

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

    public abstract boolean isEligible(ShoppingBasket shoppingBasket);

    public void addDiscount(Discount toadd){
        throw new IllegalArgumentException("Not composite discount.");
    }

    public List<Discount> getDiscounts(){
        throw new IllegalArgumentException("Not composite discount.");
    }
}
