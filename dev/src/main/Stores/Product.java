package main.Stores;


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
    }

    public Product(Product p) // Use this constructor to deep copy Product
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

    public void subtractQuantity(Integer quantity) throws Exception
    {
        if(quantity>this.quantity)
        {
            throw new Exception("Quantity to remove is larger than stock");
        }
        this.quantity = this.quantity - quantity;
    }

    public double getPrice() {
        return price;
    }

    public void addReview(ProductReview review)
    {
        this.reviews.add(review);
    }

}
