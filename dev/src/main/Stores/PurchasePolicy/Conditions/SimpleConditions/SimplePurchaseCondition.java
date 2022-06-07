package main.Stores.PurchasePolicy.Conditions.SimpleConditions;

import main.Stores.PurchasePolicy.Conditions.PurchaseCondition;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Collection;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class SimplePurchaseCondition extends PurchaseCondition {

    @Override
    public void addCondition(PurchaseCondition purchaseCondition) {
        throw new IllegalArgumentException("can't complicate simple condition.");
    }

    @Override
    public Collection<PurchaseCondition> getConditions() {
        throw new IllegalArgumentException("simple condition.");
    }
}
