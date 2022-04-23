package main.Shopping;


import main.Stores.IStore;

import java.util.HashMap;
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

    public  boolean addProductToCart(IStore IStore, String productName, int quantity) {
        synchronized (carteditLock) {
            if (baskets.containsKey(IStore.getName()))
                return baskets.get(IStore.getName()).AddProduct(productName, quantity);
            else {
                ShoppingBasket basket = new ShoppingBasket(IStore, this);
                baskets.put(IStore.getName(), basket);
                return basket.AddProduct(productName, quantity);
            }
        }
    }

    public boolean RemoveProductFromCart(IStore st, String prodName, int quantity) {
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
}
