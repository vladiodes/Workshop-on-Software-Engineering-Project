package main.Stores;


import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Stores.Discounts.Discount;
import main.Stores.PurchasePolicy.Policy;
import main.Stores.PurchasePolicy.normalPolicy;
import main.Users.User;
import main.utils.SupplyingInformation;

import java.util.LinkedList;
import java.util.List;


public class Product {
    private String productName;
    private String category;
    private List<String> keyWords;
    private String description;
    private int quantity;
    private double price;
    private List<ProductReview> reviews;
    private Policy policy;

    private Discount discount;

    public Product(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if(productName==null || productName.trim().equals(""))
            throw new IllegalArgumentException("Bad name for product!");
        if(quantity<0 || price<=0)
            throw new IllegalArgumentException("Bad product properties");

        this.productName=productName;
        this.category=category;
        this.keyWords=keyWords;
        this.description=description;
        this.quantity=quantity;
        this.price=price;
        this.reviews = new LinkedList<>();
        this.policy = new normalPolicy();
    }

    public Product(Product p) // Use this constructor to deep copy Product //TODO fix
    {
        this.productName = p.productName;
        this.category = p.category;
        this.keyWords = p.keyWords;
        this.description = p.description;
        this.quantity = p.quantity;
        this.price = p.price;
        this.reviews = p.reviews;
    }

    public String getName() {
        return productName;
    }

    public void setProperties(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if(productName==null || productName.trim().equals(""))
            throw new IllegalArgumentException("Bad name for product!");
        if(quantity<0 || price<=0)
            throw new IllegalArgumentException("Bad product properties");

        this.productName=productName;
        this.category=category;
        this.keyWords=keyWords;
        this.description=description;
        this.quantity=quantity;
        this.price=price;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getCategory() {
        return category;
    }

    public boolean hasKeyWord(String word) {
        for (String Keyword : this.keyWords)
            if (Keyword.equals(word))
                return true;
        return false;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void subtractQuantity(Integer quantity)
    {
        if(quantity>this.quantity)
        {
            throw new IllegalArgumentException("Quantity to remove is larger than stock");
        }
        this.quantity = this.quantity - quantity;
    }

    public double getCleanPrice() {
        return price;
    }


    public double getPriceWithDiscount(ShoppingBasket shoppingBasket) {
        if(discount != null)
            return this.discount.getPriceFor(this, shoppingBasket);
        else return getCleanPrice();
    }

    public Discount getDiscount() {
        return discount;
    }

    public boolean isPurchasableForAmount(Integer amount) {return this.policy.isPurchasable(this, amount);}
    public boolean isPurchasableForPrice(Double price) {
       return this.policy.isPurchasable(this, price);
    }
    public boolean Purchase(User user, Double costumePrice, int amount ,ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus){
        return this.policy.purchase(this, user, costumePrice, amount, supplying, supplyingInformation, bus);
    }
    public boolean Bid(User user, Double costumePrice, NotificationBus bus){
        return this.policy.bid(this, user, costumePrice, bus);
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public void addReview(ProductReview review)
    {
        this.reviews.add(review);
    }

}
