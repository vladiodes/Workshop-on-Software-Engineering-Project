package main.Stores.PurchasePolicy.Conditions.CompositeConditions;

import main.Stores.PurchasePolicy.Conditions.Condition;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class CompositeCondition extends Condition {

    @OneToMany
    protected List<Condition> Conditions;

    public CompositeCondition() {
        this.Conditions = new LinkedList<>();
    }

    @Override
    public void addCondition(Condition condition) {
        Conditions.add(condition);
    }

    @Override
    public List<Condition> getConditions() {
        return Conditions;
    }
}
