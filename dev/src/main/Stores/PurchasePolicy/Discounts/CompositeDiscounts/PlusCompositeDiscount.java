package main.Stores.PurchasePolicy.Discounts.CompositeDiscounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Discounts.Discount;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
public class PlusCompositeDiscount extends CompositeDiscount{
    public PlusCompositeDiscount(LocalDate until) {
        super(until);
    }

    public PlusCompositeDiscount() {

    }

    @Override
    protected Double CalculateDiscount(Double originalPrice, ShoppingBasket basket) {
        return originalPrice * (1 - getPercent(basket));
    }

    @Override
    public Double getPercent(ShoppingBasket shoppingBasket) {
        Double percent = 0.0;
        for(Discount d: this.discounts)
            if(d.isEligible(shoppingBasket))
                percent += d.getPercent(shoppingBasket);
        return percent;
    }
}
