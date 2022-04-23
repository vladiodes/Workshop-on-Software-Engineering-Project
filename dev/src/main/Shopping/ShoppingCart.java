package main.Shopping;

import main.Stores.Product;
import main.Stores.Store;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import java.util.concurrent.ConcurrentHashMap;

public class ShoppingCart {
    private ConcurrentHashMap<String, ShoppingBasket> baskets; // (store name, basket)
    private final Object carteditLock = new Object();
    public ShoppingCart() {
        this.baskets = new ConcurrentHashMap<>();
    }

    public HashMap<String, ShoppingBasket> getBasketInfo() {
        return new HashMap<>(baskets);
    }

    public  boolean addProductToCart(Store store, String productName, int quantity) {
        synchronized (carteditLock) {
            if (baskets.containsKey(store.getName()))
                return baskets.get(store.getName()).AddProduct(productName, quantity);
            else {
                ShoppingBasket basket = new ShoppingBasket(store, this);
                baskets.put(store.getName(), basket);
                return basket.AddProduct(productName, quantity);
            }
        }
    }

    public boolean RemoveProductFromCart(Store st, String prodName, int quantity) {
        if (!baskets.containsKey(st.getName()))
            throw new IllegalArgumentException("Basket for that store doesn't exist yet.");
        synchronized (carteditLock) {
            ShoppingBasket basket = baskets.get(st.getName());
            basket.RemoveProduct(prodName, quantity);
            if (basket.getAmountOfProducts() == 0)
                this.baskets.remove(st.getName());
            return true;
        }
    }
	
	public boolean isProductInCart(String productName, String storeName) {
        if(!baskets.containsKey(storeName))
        {
            return false;
        }
        ShoppingBasket sb = baskets.get(storeName);
        HashMap<Product, Integer> productsQuantities = sb.getProductsAndQuantities();
        for(Product p : productsQuantities.keySet())
        {
            if(p.getName().equals(productName))
            {
                return true;
            }
        }
        return false;
    }

    public Product getProduct(String productName, String storeName) {
        if(!baskets.containsKey(storeName))
        {
            return null;
        }
        ShoppingBasket sb = baskets.get(storeName);
        HashMap<Product, Integer> productsQuantities = sb.getProductsAndQuantities();
        for(Product p : productsQuantities.keySet())
        {
            if(p.getName().equals(productName))
            {
                return p;
            }
        }
        return null;
    }

    public boolean isStoreInCart(String storeName) {
        return baskets.containsKey(storeName);
    }

    public Store getStore(String storeName) {
        if(!baskets.containsKey(storeName))
            return null;
        return baskets.get(storeName).getStore();
    }
}