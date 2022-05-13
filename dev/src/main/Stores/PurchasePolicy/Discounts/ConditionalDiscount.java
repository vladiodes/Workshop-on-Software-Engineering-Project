package main.Stores.PurchasePolicy.Discounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.Condition;

import java.time.LocalDate;

public class ConditionalDiscount extends SingleDiscount{
    private Condition condition;
    public ConditionalDiscount(LocalDate until, Double percent, Condition cond) {
        super(until, percent);
        this.condition = cond;
    }

    @Override
    protected Double getPercent(ShoppingBasket shoppingBasket) {
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
