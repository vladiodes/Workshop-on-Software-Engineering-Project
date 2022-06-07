package main.Stores.PurchasePolicy.Conditions;

import main.Shopping.ShoppingBasket;
import java.util.Collection;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PurchaseCondition {
    @Id
    @GeneratedValue
    private int id;

    private int id_in_store;
    public abstract boolean pass(ShoppingBasket shoppingBasket);
    public abstract void addCondition(PurchaseCondition purchaseCondition);
    public abstract Collection<PurchaseCondition> getConditions();

    public int getId() {
        return id;
    }

    public int getId_in_store() {
        return id_in_store;
    }

    public void setId_in_store(int id_in_store) {
        this.id_in_store = id_in_store;
    }
}
