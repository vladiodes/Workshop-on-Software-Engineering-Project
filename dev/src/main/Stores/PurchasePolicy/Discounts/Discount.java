package main.Stores.PurchasePolicy.Discounts;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.Condition;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Discount {
    @Id
    @GeneratedValue
    private int id;
    private LocalDate until;

    public Discount(LocalDate until) {
        this.until = until;
    }

    public Discount() {

    }

    public Double getPriceFor(Double originalPrice, ShoppingBasket shoppingBasket) {
        if (until.isAfter(LocalDate.now()) && isEligible(shoppingBasket))
            return CalculateDiscount(originalPrice, shoppingBasket);
        else return originalPrice;
    }

    protected abstract Double CalculateDiscount(Double originalPrice, ShoppingBasket basket);

    public abstract Double getPercent(ShoppingBasket shoppingBasket);

    public LocalDate getUntil() {
        return until;
    }

    public void setUntil(LocalDate until) {
        this.until = until;
    }

    public abstract boolean isEligible(ShoppingBasket shoppingBasket);

    public void addDiscount(Discount d) {
        throw new IllegalArgumentException("Not a composite discount.");
    }

    public List<Discount> getDiscounts(){
        throw new IllegalArgumentException("Not a composite discount.");
    }

    public void setCondition(Condition cond) {
        throw new IllegalArgumentException("this discount doesn't have conditions.");
    }

    public int getId() {
        return id;
    }
}
