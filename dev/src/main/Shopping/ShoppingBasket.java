package main.Shopping;

import main.Stores.Product;
import main.Stores.Store;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ShoppingBasket {
    private ConcurrentHashMap<Product,Integer> productsQuantity;
    private Store store;
    private ShoppingCart cart;
    private final Object basketEditLock = new Object();

    public ShoppingBasket(Store store, ShoppingCart cart){
        this.cart = cart;
        this.store = store;
        productsQuantity=new ConcurrentHashMap<>();
    }

    private int setProductQuantity(String prodName, int additiveQuanity) {
        Product prod = null;
        for (Product p : productsQuantity.keySet())
            if (p.getName().equals(prodName)){
                prod = p;
                break;
            }
        if (prod == null)
            throw new IllegalArgumentException(String.format("Product %s doesn't exist in the basket.", prod));
        int newValue = productsQuantity.get(prod) + additiveQuanity;
        if (newValue <= 0)
            productsQuantity.remove(prod);
        else
            productsQuantity.put(prod, newValue);
        return newValue;
    }

    public synchronized boolean AddProduct (String prodName, int quantity) {
        synchronized (basketEditLock) {
            Product prodToAdd = this.store.getProduct(prodName);
            if (prodToAdd == null)
                throw new IllegalArgumentException(String.format("Product %s doesn't exist in the store.", prodName));
            for (Product pr : productsQuantity.keySet())
                if (pr.getName().equals(prodName)) {
                    productsQuantity.put(pr, productsQuantity.get(pr) + quantity);
                    return true;
                }
            productsQuantity.put(prodToAdd, quantity);
            return true;
        }
    }

    public synchronized  int RemoveProduct(String productName, int quantity) {
        synchronized (basketEditLock) {
            return setProductQuantity(productName, quantity * -1);
        }
    }

    public int getAmountOfProducts(){
        return productsQuantity.size();
    }

    public HashMap<Product, Integer> getProductsAndQuantities() {
        return new HashMap<>(productsQuantity);
    }

    public Store getStore() {
        return store;
    }
}

