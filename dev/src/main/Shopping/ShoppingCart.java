package main.Shopping;



import main.Stores.IStore;


import java.util.*;

import main.Stores.Product;

import java.util.HashMap;

import java.util.concurrent.ConcurrentHashMap;

public class ShoppingCart {
    private ConcurrentHashMap<String, ShoppingBasket> baskets; // (store name, basket)
    private final Object carteditLock = new Object();
    public ShoppingCart() {
        this.baskets = new ConcurrentHashMap<>();
    }

    public ShoppingCart(ShoppingCart oldCart) //use this constructor to deep copy a ShoppingCart
    {
        ConcurrentHashMap<String, ShoppingBasket> oldBaskets = oldCart.getBaskets();
        ConcurrentHashMap<String, ShoppingBasket> newBaskets = new ConcurrentHashMap<>();

        for(HashMap.Entry<String , ShoppingBasket> element : oldBaskets.entrySet())
        {
            newBaskets.put(element.getKey(), new ShoppingBasket(element.getValue()));
        }
        this.baskets = newBaskets;
    }
    public HashMap<String, ShoppingBasket> getBasketInfo() {
        return new HashMap<>(baskets);
    }

    public  boolean addProductToCart(IStore IStore, String productName, int quantity) {
        synchronized (carteditLock) {
            if (baskets.containsKey(IStore.getName()))
                return baskets.get(IStore.getName()).AddProduct(productName, quantity);
            else {
                if (!IStore.getIsActive())
                    throw new IllegalArgumentException("Store is not active.");
                if (!IStore.getProductsByName().containsKey(productName))
                    throw new IllegalArgumentException("Product does not exist in store");
                ShoppingBasket basket = new ShoppingBasket(IStore);
                baskets.put(IStore.getName(), basket);
                return basket.AddProduct(productName, quantity);
            }
        }
    }

    public  boolean setCostumeProductPrice(IStore IStore, String productName, double price) {
        synchronized (carteditLock) {
            if (baskets.containsKey(IStore.getName()))
                return baskets.get(IStore.getName()).setCostumePriceForProduct(productName, price);
            else {
                throw new IllegalArgumentException("No basket for this store.");
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
	
	public boolean isProductInCart(String productName, String storeName) {
        if(!baskets.containsKey(storeName))
        {
            return false;
        }
        ShoppingBasket sb = baskets.get(storeName);
        ConcurrentHashMap<Product, Integer> productsQuantities = sb.getProductsAndQuantities();
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
        ConcurrentHashMap<Product, Integer> productsQuantities = sb.getProductsAndQuantities();
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

    public IStore getStore(String storeName) {
        if(!baskets.containsKey(storeName))
            return null;
        return baskets.get(storeName).getStore();
    }

    public double getPrice(){
        double result = 0;
        for (Map.Entry<String, ShoppingBasket> basketEntry : this.baskets.entrySet())
            result += basketEntry.getValue().getPrice();
        return result;
    }

    public ConcurrentHashMap<String, ShoppingBasket> getBaskets() {
        return baskets;
    }

    public ShoppingBasket getBasket(String storeName){
        if (!this.getBaskets().containsKey(storeName))
            throw new IllegalArgumentException("Basket for that store doesn't exist in this cart.");
        return getBaskets().get(storeName);
    }

    public HashMap<Product, Integer> getProducts() {
        HashMap<Product, Integer> res = new HashMap<>();
        for (Map.Entry<String, ShoppingBasket> basketEntry : this.baskets.entrySet())
            res.putAll(basketEntry.getValue().getProductsAndQuantities());
        return res;
    }

    /***
     * @return amount of unique products.
     */
    public int getAmountOfProducts(){
        int res = 0;
        for(ShoppingBasket basket : this.baskets.values())
            res+=basket.getAmountOfProducts();
        return res;
    }

    /***
     *
     * @return true/false if cart is purchasable.
     */
    public boolean ValidateCart() {
        boolean res = getAmountOfProducts() > 0;
        for(ShoppingBasket basket : this.baskets.values())
            res &= basket.ValidateBasket();
        return res;
    }
}