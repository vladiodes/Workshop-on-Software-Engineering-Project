package main.Stores.PurchasePolicy.Discounts.CompositeDiscounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Discounts.Discount;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class CompositeDiscount extends Discount {
    @OneToMany
    protected List<Discount> discounts;

    public CompositeDiscount(LocalDate until) {
        super(until);
        this.discounts = new LinkedList<>();
    }

    public CompositeDiscount() {

    }

    public void addDiscount(Discount d) {
        discounts.add(d);
    }

    @Override
    public boolean isEligible(ShoppingBasket shoppingBasket) {
        boolean output = false;
        for (Discount d : this.discounts)
            output |= d.isEligible(shoppingBasket);
        return  output;
    }

    protected abstract Double CalculateDiscount(Double originalPrice, ShoppingBasket basket);

    public List<Discount> getDiscounts(){
        return discounts;
    }
}
