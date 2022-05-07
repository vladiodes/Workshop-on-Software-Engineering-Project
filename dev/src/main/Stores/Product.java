package main.Stores;


import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.NotificationBus;
import main.Shopping.ShoppingBasket;
import main.Stores.Discounts.Discount;
import main.Stores.PurchasePolicy.Policy;
import main.Stores.PurchasePolicy.normalPolicy;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import java.util.LinkedList;
import java.util.List;


public class Product {
    private String productName;
    private String category;
    private List<String> keyWords;
    private String description;
    private int quantity;
    private List<ProductReview> reviews;
    private Policy policy;

    private IStore store;

    public Product(IStore store,String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if(productName==null || productName.trim().equals(""))
            throw new IllegalArgumentException("Bad name for product!");
        if(quantity<0 || price<=0)
            throw new IllegalArgumentException("Bad product properties");

        this.productName=productName;
        this.category=category;
        this.keyWords=keyWords;
        this.description=description;
        this.quantity=quantity;
        this.reviews = new LinkedList<>();
        this.policy = new normalPolicy(price, store);
        this.store=store;
    }

    public Product(Product p) // Use this constructor to deep copy Product //TODO fix
    {
        this.productName = p.productName;
        this.category = p.category;
        this.keyWords = p.keyWords;
        this.description = p.description;
        this.quantity = p.quantity;
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
        this.setPrice(price);
    }

    public void setPrice(Double price){
        this.policy.setOriginalPrice(price);
    }

    public void setPolicy(Policy policy, NotificationBus bus) {
        this.policy.close(bus);
        this.policy = policy;
    }

    public String getCategory() {
        return category;
    }

    public boolean deliveredImmediately(){ return policy.deliveredImmediately();}

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
       return this.policy.getOriginalPrice();
    }


    public double getCurrentPrice(ShoppingBasket basket) {
        return policy.getCurrentPrice(basket);
    }

    public boolean isPurchasableForAmount(Integer amount) {return this.policy.isPurchasable(this, amount);}
    public boolean isPurchasableForPrice(Double price, int amount) {
       return this.policy.isPurchasable(this, price, amount);
    }
    public boolean Purchase(User user, Double costumePrice, int amount , ISupplying supplying, SupplyingInformation supplyingInformation, NotificationBus bus, PaymentInformation paymentInformation, IPayment payment){
        return this.policy.purchase(this, user, costumePrice, amount, supplying, supplyingInformation, bus, paymentInformation , payment );
    }
    public boolean Bid(Bid bid){
        return this.policy.bid(bid);
    }

    public void setDiscount(Discount discount) {
        this.policy.setDiscount(discount);
    }

    public void addReview(ProductReview review)
    {
        this.reviews.add(review);
    }

    public IStore getStore() {
        return store;
    }

    public void bid(Bid bid) {
        this.policy.bid(bid);
    }
}
