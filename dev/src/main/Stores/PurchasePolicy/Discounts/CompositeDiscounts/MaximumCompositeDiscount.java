package main.Stores.PurchasePolicy.Discounts.CompositeDiscounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Discounts.Discount;

import javax.persistence.Entity;
import java.time.LocalDate;
@Entity
public class MaximumCompositeDiscount extends  CompositeDiscount{
    public MaximumCompositeDiscount(LocalDate until) {
        super(until);
    }

    public MaximumCompositeDiscount() {

    }


    @Override
    protected Double CalculateDiscount(Double originalPrice, ShoppingBasket basket) {
        return originalPrice*(1-getPercent(basket));
    }

    @Override
    public Double getPercent(ShoppingBasket shoppingBasket) {
        Double percent = 0.0;
        for(Discount d : this.discounts) {
            double dpercent = d.getPercent(shoppingBasket);
            if (percent < dpercent && d.isEligible(shoppingBasket))
                percent = dpercent;
        }
        return percent;
    }
}
