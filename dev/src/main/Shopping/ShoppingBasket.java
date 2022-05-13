package main.Shopping;


import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Stores.IStore;
import main.Stores.Product;
import main.Users.User;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ShoppingBasket {
    private ConcurrentHashMap<Product,Integer> productsQuantity;
    private WeakHashMap<Product, Double> costumePrice;
    private final IStore store;
    private final Object basketEditLock = new Object();

    private final List<String> discountPasswords = new LinkedList<>();

    public ShoppingBasket(IStore store){
        this.store = store;
        productsQuantity=new ConcurrentHashMap<>();
        costumePrice=new WeakHashMap<>();
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
        if (newValue <= 0) {
            productsQuantity.remove(prod);
            costumePrice.remove(prod);
        }
        else
            productsQuantity.put(prod, newValue);
        return Math.max(0, newValue);
    }

    public boolean AddProduct (String prodName, int quantity) {
        return addProduct(this.store.getProduct(prodName), quantity);
    }

    private boolean addProduct(Product prodToAdd, int quantity) {
        synchronized (basketEditLock) {
            if (quantity <= 0)
                throw new IllegalArgumentException("Can't add negative number of product.");
            if (!prodToAdd.isPurchasableForAmount(quantity))
                throw new IllegalArgumentException("amount too high for product.");
            if (!store.getIsActive())
                throw new IllegalArgumentException(String.format("Product %s isnt available.", prodToAdd.getName()));
            for (Product pr : productsQuantity.keySet())
                if (pr.getName().equals(prodToAdd.getName())) {
                    productsQuantity.put(pr, productsQuantity.get(pr) + quantity);
                    return true;
                }
            productsQuantity.put(prodToAdd, quantity);
        }
        return true;
    }

    //used when adding product with costume price, used for raffles.
    public boolean setCostumePriceForProduct(String prodName, double price, User user) {
        Product prodToSet = this.store.getProduct(prodName);
        if(prodToSet.isPurchasableForPrice(price, productsQuantity.get(prodToSet), user))
            this.costumePrice.put(prodToSet, price);
        else throw new IllegalArgumentException("custom price is invalid.");
        return true;
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

    public void purchaseBasket(User user, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment)
    {
        store.purchaseBasket(user, supplying, supplyingInformation, paymentInformation, payment,this);
    }

    public double getPrice(User user) {
        double res = 0;
        for (Map.Entry<Product, Integer> en : productsQuantity.entrySet())
            if(!costumePrice.containsKey(en.getKey()))
                res += en.getKey().getCurrentPrice(user) * en.getValue();
            else res += costumePrice.get(en.getKey()) * en.getValue();
        return res;
    }

    public Double getCostumePriceForProduct(Product product) {
        return this.costumePrice.get(product);
    }


    /**
     * @return true/false depending if the basket is purchasable.
     */
    public boolean ValidateBasket(User user) {
        boolean res = true;
        for (Map.Entry<Product, Integer> ent: this.getProductsAndQuantities().entrySet() ) {
            res &= store.getIsActive() && ent.getKey().isPurchasableForAmount(ent.getValue());
            if(this.costumePrice.containsKey(ent.getKey()))
                res &= ent.getKey().isPurchasableForPrice(costumePrice.get(ent.getKey()), ent.getValue(), user);
        }
        return res;
    }

    public Map<Product, Integer> getProductsAndQuantitiesForPurchase(User user) {
        Map<Product, Integer> output= new HashMap<>();
        for (Map.Entry<Product, Integer> ent: this.getProductsAndQuantities().entrySet())
            if(ent.getKey().deliveredImmediately(user))
                output.put(ent.getKey(), ent.getValue());
        return output;
    }
}

