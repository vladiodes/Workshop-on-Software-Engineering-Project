package main.Stores.PurchasePolicy.Conditions.CompositeConditions;

import main.Stores.PurchasePolicy.Conditions.PurchaseCondition;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class CompositePurchaseCondition extends PurchaseCondition {

    @OneToMany
    protected List<PurchaseCondition> purchaseConditions;

    public CompositePurchaseCondition() {
        this.purchaseConditions = new LinkedList<>();
    }

    @Override
    public void addCondition(PurchaseCondition purchaseCondition) {
        purchaseConditions.add(purchaseCondition);
    }

    @Override
    public List<PurchaseCondition> getConditions() {
        return purchaseConditions;
    }
}
