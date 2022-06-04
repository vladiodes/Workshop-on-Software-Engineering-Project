package main.Stores.PurchasePolicy.Conditions.CompositeConditions;

import main.Stores.PurchasePolicy.Conditions.Condition;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public abstract class CompositeCondition extends Condition {

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
