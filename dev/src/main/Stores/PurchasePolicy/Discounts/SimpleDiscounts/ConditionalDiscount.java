package main.Stores.PurchasePolicy.Discounts.SimpleDiscounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.PurchaseCondition;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class ConditionalDiscount extends SingleDiscount{
    @OneToOne
    private PurchaseCondition purchaseCondition;
    public ConditionalDiscount(LocalDate until, Double percent, PurchaseCondition cond) {
        super(until, percent);
        this.purchaseCondition = cond;
    }

    public ConditionalDiscount() {

    }

    @Override
    public Double getPercent(ShoppingBasket shoppingBasket) {
        if(isEligible(shoppingBasket))
            return  this.percent;
        else return 0.0;
    }

    @Override
    public boolean isEligible(ShoppingBasket shoppingBasket) {
        return purchaseCondition.pass(shoppingBasket);
    }

    @Override
    public void setCondition(PurchaseCondition cond){
        this.purchaseCondition = cond;
    }
}
