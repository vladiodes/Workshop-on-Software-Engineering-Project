package main.Stores;

import java.util.List;

public class Product {
    private String productName;
    private String category;
    private List<String> keyWords;
    private String description;
    private int quantity;
    private double price;

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

    public double getPrice() {
        return price;
    }
}
