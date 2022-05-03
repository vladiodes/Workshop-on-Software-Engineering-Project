package main.Stores.Discounts;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;

import java.time.LocalDate;

public abstract class Discount {
    private LocalDate until;

    public  Double getPriceFor(Product product, ShoppingBasket shoppingBasket){
        if (until.isAfter(LocalDate.now()))
            return CalculateDiscount(product, shoppingBasket);
        else return product.getCleanPrice();
    }

    protected abstract Double CalculateDiscount(Product product, ShoppingBasket shoppingBasket);

    public LocalDate getUntil() {
        return until;
    }

    public void setUntil(LocalDate until) {
        this.until = until;
    }

    @Override
    public abstract String toString();
}
