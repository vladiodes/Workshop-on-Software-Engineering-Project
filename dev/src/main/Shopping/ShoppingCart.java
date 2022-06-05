package main.Shopping;

import java.util.*;

import main.Persistence.DAO;
import main.Stores.Product;
import main.Stores.Store;
import main.Users.User;
import javax.persistence.*;
import java.util.concurrent.ConcurrentHashMap;


@Entity
public class ShoppingCart {
    @Id
    @GeneratedValue
    private int cart_id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "shopping_cart_baskets",
    joinColumns = {@JoinColumn(name="cart_id",referencedColumnName = "cart_id")},
            inverseJoinColumns = {@JoinColumn(name="shopping_basket_id",referencedColumnName = "id")})
    @MapKey(name="store_name")
    private Map<String, ShoppingBasket> baskets; // (store name, basket)
    @Transient
    private final Object carteditLock = new Object();
    @OneToOne
    private User user;

    public ShoppingCart(User user) {
        this.baskets = Collections.synchronizedMap(new HashMap<>());
        this.user = user;
    }

    public ShoppingCart() {

    }

    public HashMap<String, ShoppingBasket> getBasketInfo() {
        return new HashMap<>(baskets);
    }

    public  boolean addProductToCart(Store IStore, String productName, int quantity) {
        synchronized (carteditLock) {
            if (baskets.containsKey(IStore.getName())) {
                boolean output= baskets.get(IStore.getName()).AddProduct(productName, quantity);
                DAO.getInstance().merge(baskets.get(IStore.getName()));
                return output;
            }
            else {
                if (!IStore.getIsActive())
                    throw new IllegalArgumentException("Store is not active.");
                if (!IStore.getProductsByName().containsKey(productName))
                    throw new IllegalArgumentException("Product does not exist in store");
                ShoppingBasket basket = new ShoppingBasket(IStore, this.user);

                DAO.getInstance().persist(basket);

                baskets.put(IStore.getName(), basket);
                boolean output= basket.AddProduct(productName, quantity);
                DAO.getInstance().merge(basket);
                return output;
            }
        }
    }

    public  boolean setCostumeProductPrice(Store IStore, String productName, double price, User user) {
        synchronized (carteditLock) {
            if (baskets.containsKey(IStore.getName()))
                return baskets.get(IStore.getName()).setCostumePriceForProduct(productName, price, user);
            else {
                throw new IllegalArgumentException("No basket for this store.");
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
        Map<Product, Integer> productsQuantities = sb.getProductsAndQuantities();
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
        Map<Product, Integer> productsQuantities = sb.getProductsAndQuantities();
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

    public double getPrice(){
        double result = 0;
        for (Map.Entry<String, ShoppingBasket> basketEntry : this.baskets.entrySet())
            result += basketEntry.getValue().getPrice();
        return result;
    }

    public Map<String, ShoppingBasket> getBaskets() {
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
    public boolean ValidateCart(User user) {
        boolean res = getAmountOfProducts() > 0;
        for(ShoppingBasket basket : this.baskets.values())
            res &= basket.ValidateBasket(user);
        return res;
    }

    public Map<Product, Integer> getProductsForPurchase(User user) {
        Map<Product,Integer> output = new HashMap<>();
        for(ShoppingBasket basket : this.baskets.values())
            output.putAll(basket.getProductsAndQuantitiesForPurchase(user));
        return output;
    }
}