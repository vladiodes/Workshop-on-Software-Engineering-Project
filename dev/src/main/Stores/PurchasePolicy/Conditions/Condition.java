package main.Stores.PurchasePolicy.Conditions;

import main.Shopping.ShoppingBasket;

import java.util.Collection;

public interface Condition  {
    public boolean pass(ShoppingBasket shoppingBasket);
    public void addCondition(Condition condition);
    public Collection<Condition> getConditions();
}
