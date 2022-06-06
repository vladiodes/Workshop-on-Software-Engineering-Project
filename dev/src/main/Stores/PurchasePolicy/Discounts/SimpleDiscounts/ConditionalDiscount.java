package main.Stores.PurchasePolicy.Discounts.SimpleDiscounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.Condition;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class ConditionalDiscount extends SingleDiscount{
    @OneToOne
    private Condition condition;
    public ConditionalDiscount(LocalDate until, Double percent, Condition cond) {
        super(until, percent);
        this.condition = cond;
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
        return condition.pass(shoppingBasket);
    }

    @Override
    public void setCondition(Condition cond){
        this.condition = cond;
    }
}
