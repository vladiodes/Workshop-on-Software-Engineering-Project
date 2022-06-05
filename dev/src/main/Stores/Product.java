package main.Stores;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.PurchasePolicy.Discounts.Discount;
import main.Stores.PurchasePolicy.ProductPolicy.Policy;
import main.Stores.PurchasePolicy.ProductPolicy.normalPolicy;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;


@Entity
public class Product {

    @Id
    @GeneratedValue
    private int id;
    private String productName;
    private String category;

    @ElementCollection
    private List<String> keyWords;
    private String description;
    private int quantity;

    @Transient
    private List<ProductReview> reviews;
    @Transient
    private Policy policy;
    @OneToOne
    private Store store;

    public Product(Store store,String productName, String category, List<String> keyWords, String description, int quantity, double price) {
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

    public Product() {

    }

    public String getName() {
        return productName;
    }

    public synchronized void setProperties(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
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

    public void setPolicy(Policy policy) {
        this.policy.close();
        this.policy = policy;
    }

    public String getCategory() {
        return category;
    }

    public boolean deliveredImmediately(User user){ return policy.deliveredImmediately(user);}

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

    public synchronized void subtractQuantity(Integer quantity)
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


    public double getCurrentPrice(User user) {
        return policy.getCurrentPrice(user);
    }

    public boolean isPurchasableForAmount(Integer amount) {return this.policy.isPurchasable(this, amount);}
    public boolean isPurchasableForPrice(Double price, int amount, User user) {
       return this.policy.isPurchasable(this, price, amount, user);
    }
    public boolean Purchase(User user, Double costumePrice, int amount , ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment){
        return this.policy.productPurchased(this, user, costumePrice, amount, supplying, supplyingInformation, paymentInformation , payment );
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

    public Store getStore() {
        return store;
    }

    public boolean bid(Bid bid) {
        return this.policy.bid(bid);
    }

    public List<Bid> getUserBids() {
        return policy.getBids();
    }

    public void ApproveBid(User user, User apporvingUser) throws Exception {
        policy.approveBid(user, apporvingUser);
    }

    public void DeclineBid(User user) {
        policy.declineBid(user);
    }

    public void counterOfferBid(User user, Double offer) {
        policy.counterOfferBid(user, offer);
    }

    public boolean isAddableToBasket() {
        return policy.isAddableToBasket();
    }
}
