package main.Stores;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Store implements IStore {

    private ConcurrentHashMap<String,Product> productsByName;

    public Store(){
        this.productsByName=new ConcurrentHashMap<>();
    }

    public boolean addProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        if(productsByName.containsKey(productName))
            throw new IllegalArgumentException("There's already such product with this name in the store");

        Product product=new Product(productName,category,keyWords,description,quantity,price);
        productsByName.put(productName,product);
        return true;
    }

    public boolean updateProduct(String productName, String category, List<String> keyWords, String description, int quantity, double price) {
        Product product=productsByName.get(productName);
        if(product==null)
            throw new IllegalArgumentException("No such product in the store!");
        String prevName=product.getName();
        if(!prevName.equals(productName)) { //name is changed
            if(productsByName.containsKey(productName)) //the name is already taken
                throw new IllegalArgumentException("There's already a product with that name!");
        }
        product.setProperties(productName,category,keyWords,description,quantity,price);

        productsByName.remove(prevName);
        productsByName.put(productName,product);
        return true;
    }
}
