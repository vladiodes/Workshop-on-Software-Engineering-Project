package main.Stores.PurchasePolicy.Conditions;

import main.Shopping.ShoppingBasket;
import java.util.Collection;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Condition  {
    @Id
    @GeneratedValue
    private int id;
    public abstract boolean pass(ShoppingBasket shoppingBasket);
    public abstract void addCondition(Condition condition);
    public abstract Collection<Condition> getConditions();

    public int getId() {
        return id;
    }
}
