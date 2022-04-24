package main.Shopping;

import main.Stores.IStore;
import main.Stores.Product;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ShoppingBasket {
    private ConcurrentHashMap<Product,Integer> productsQuantity;
    private IStore store;
    private ShoppingCart cart;
    private final Object basketEditLock = new Object();

    public ShoppingBasket(IStore store, ShoppingCart cart){
        this.cart = cart;
        this.store = store;
        productsQuantity=new ConcurrentHashMap<>();
    }


    /*
        ConcurrentHashMap<String, ShoppingBasket> oldBaskets = oldCart.getBaskets();
        ConcurrentHashMap<String, ShoppingBasket> newBaskets = new ConcurrentHashMap<>();

        for(HashMap.Entry<String , ShoppingBasket> element : oldBaskets.entrySet())
        {
            newBaskets.put(element.getKey(), new ShoppingBasket(element.getValue()));
        }
        this.baskets = newBaskets;*/

    public ShoppingBasket(ShoppingBasket oldShoppingBasket) //Use this constructor to deep copy ShoppingBasket (only productsQuantity)
    {
        ConcurrentHashMap<Product,Integer> oldProductsQuantity = oldShoppingBasket.getProductsAndQuantities();
        ConcurrentHashMap<Product,Integer> newProductsQuantity = new ConcurrentHashMap<>();

        for(HashMap.Entry<Product, Integer> element : oldProductsQuantity.entrySet())
        {
            newProductsQuantity.put(new Product(element.getKey()), element.getValue());
        }

        this.cart = oldShoppingBasket.cart;
        this.store = oldShoppingBasket.store;
        this.productsQuantity = newProductsQuantity;
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

    public ConcurrentHashMap<Product, Integer> getProductsAndQuantities() {
        return new ConcurrentHashMap<>(productsQuantity);
    }

    public IStore getStore() {
        return store;
    }

    public void purchaseBasket() throws Exception
    {
        for(ConcurrentHashMap.Entry<Product, Integer> element : this.productsQuantity.entrySet())
        {
            store.subtractProductQuantity(element.getKey(), element.getValue());
        }

    }
}

