package main.Stores.Discounts;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;

import java.time.LocalDate;

public class SecretDiscount extends DirectDiscount{
    private final String password;

    public SecretDiscount(Double percent, LocalDate until, String password) {
        super(percent, until);
        this.password = password;
    }
    protected Double CalculateDiscount(Double originalPrice, ShoppingBasket shoppingBasket) {
        if (shoppingBasket != null && shoppingBasket.hasDiscountPassword(password))
            return super.CalculateDiscount(originalPrice, shoppingBasket);
        else return originalPrice;
    }

    @Override
    public String toString(){
        return super.toString() + " available with secret code only.";
    }
}
