package main.Stores.PurchasePolicy.Discounts;

import main.Shopping.ShoppingBasket;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public abstract class CompositeDiscount extends  Discount{
    protected List<Discount> discounts;

    public CompositeDiscount(LocalDate until) {
        super(until);
        this.discounts = new LinkedList<>();
    }
    public void addDiscount(Discount d) {
        discounts.add(d);
    }

    @Override
    public boolean isEligible(ShoppingBasket shoppingBasket) {
        boolean output = false;
        for (Discount d : this.discounts)
            output |= d.isEligible(shoppingBasket);
        return  output;
    }

    protected abstract Double CalculateDiscount(Double originalPrice, ShoppingBasket basket);

    public List<Discount> getDiscounts(){
        return discounts;
    }
}
