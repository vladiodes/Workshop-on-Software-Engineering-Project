package main.Shopping;


import main.NotificationBus;
import main.Stores.IStore;
import main.Stores.Product;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShoppingBasket {
    private ConcurrentHashMap<Product,Integer> productsQuantity;
    private final IStore store;
    private final Object basketEditLock = new Object();

    private final List<String> discountPasswords = new LinkedList<>();

    public ShoppingBasket(IStore store){
        this.store = store;
        productsQuantity=new ConcurrentHashMap<>();
    }

    public ShoppingBasket(ShoppingBasket oldShoppingBasket) //Use this constructor to deep copy ShoppingBasket (only productsQuantity)
    {
        ConcurrentHashMap<Product,Integer> oldProductsQuantity = oldShoppingBasket.getProductsAndQuantities();
        ConcurrentHashMap<Product,Integer> newProductsQuantity = new ConcurrentHashMap<>();

        for(HashMap.Entry<Product, Integer> element : oldProductsQuantity.entrySet())
        {
            newProductsQuantity.put(new Product(element.getKey()), element.getValue());
        }

        this.store = oldShoppingBasket.store;
        this.productsQuantity = newProductsQuantity;
    }

    public void addDiscountPassword(String pass){
        discountPasswords.add(pass);
    }

    public boolean hasAmount(Product product, Integer amount){
        return productsQuantity.get(product) >= amount;
    }

    public boolean hasDiscountPassword(String pass){
        for (String userPass : this.discountPasswords)
            if (userPass.equals(pass))
                return true;
        return false;
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
        return Math.max(0, newValue);
    }

    public boolean AddProduct (String prodName, int quantity) {
        synchronized (basketEditLock) {
            Product prodToAdd = this.store.getProduct(prodName);
            if (prodToAdd == null)
                throw new IllegalArgumentException(String.format("Product %s doesn't exist in the store.", prodName));
            if (quantity <= 0)
                throw new IllegalArgumentException("Can't add negative number of product.");
            if (!store.ValidateProduct(prodToAdd, quantity))
                throw new IllegalArgumentException(String.format("Product %s isnt available.", prodName));
            for (Product pr : productsQuantity.keySet())
                if (pr.getName().equals(prodName)) {
                    productsQuantity.put(pr, productsQuantity.get(pr) + quantity);
                    return true;
                }
            productsQuantity.put(prodToAdd, quantity);
            return true;
        }
    }

    public int RemoveProduct(String productName, int quantity) {
        synchronized (basketEditLock) {
            if(quantity <= 0)
                throw new IllegalArgumentException("Cant remove negative number");
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

    public void purchaseBasket(NotificationBus bus)
    {
        store.purchaseBasket(bus, this);
    }

    public double getPrice() {
        double res = 0;
        for (Map.Entry<Product, Integer> en : productsQuantity.entrySet())
            res += en.getKey().getPriceWithDiscount(this) * en.getValue();
        return res;
    }

    /**
     * @return true/false depending if the basket is purchesable.
     */
    public boolean ValidateBasket() {
        boolean res = true;
        for (Map.Entry<Product, Integer> ent: this.getProductsAndQuantities().entrySet() )
            res &= this.store.ValidateProduct(ent.getKey(), ent.getValue());
        return res;
    }
}

