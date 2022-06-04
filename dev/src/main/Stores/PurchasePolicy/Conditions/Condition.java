package main.Stores.PurchasePolicy.Conditions;

import main.Shopping.ShoppingBasket;
import java.util.Collection;
import javax.persistence.*;

public abstract class Condition  {
    private int id;
    public abstract boolean pass(ShoppingBasket shoppingBasket);
    public abstract void addCondition(Condition condition);
    public abstract Collection<Condition> getConditions();
}
