package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.Condition;

import java.util.Collection;

public abstract class SimpleCondition implements Condition {

    @Override
    public void addCondition(Condition condition) {
        throw new IllegalArgumentException("can't complicate simple condition.");
    }

    @Override
    public Collection<Condition> getConditions() {
        throw new IllegalArgumentException("simple condition.");
    }
}
