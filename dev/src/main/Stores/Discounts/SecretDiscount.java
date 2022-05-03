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
    protected Double CalculateDiscount(Product product, ShoppingBasket shoppingBasket) {
        if (shoppingBasket.hasDiscountPassword(password))
            return super.CalculateDiscount(product, shoppingBasket);
        else return product.getCleanPrice();
    }

    @Override
    public String toString(){
        return super.toString() + " available with secret code only.";
    }
}
