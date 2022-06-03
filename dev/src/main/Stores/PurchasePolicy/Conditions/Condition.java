package main.Stores.PurchasePolicy.Conditions;

import main.Shopping.ShoppingBasket;
import java.util.Collection;
import javax.persistence.*;

@Entity
@Table(name = "conditions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public interface Condition  {
    public boolean pass(ShoppingBasket shoppingBasket);
    public void addCondition(Condition condition);
    public Collection<Condition> getConditions();
}
