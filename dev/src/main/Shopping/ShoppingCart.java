package main.Shopping;

import main.Stores.Product;
import main.Stores.Store;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class ShoppingCart {
    private List<ShoppingBasket> baskets;

    public boolean isProductInCart(String productName, String storeName) {
        for(ShoppingBasket sb : baskets)
        {
            if(sb.getStore().getName().equals(storeName))
            {
                HashMap<Product, Integer> productsQuantity = sb.getProductsQuantity();
                for(Product p : productsQuantity.keySet())
                {
                    if(p.getName().equals(productName))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Product getProduct(String productName, String storeName) {
        for(ShoppingBasket sb : baskets)
        {
            if(sb.getStore().getName().equals(storeName))
            {
                HashMap<Product, Integer> productsQuantity = sb.getProductsQuantity();
                for(Product p : productsQuantity.keySet())
                {
                    if(p.getName().equals(productName))
                    {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    public boolean isStoreInCart(String storeName) {
        for(ShoppingBasket sb : baskets)
        {
            if(sb.getStore().getName().equals(storeName))
            {
                return true;
            }
        }
        return false;
    }

    public Store getStore(String storeName) {
        for(ShoppingBasket sb : baskets)
        {
            if(sb.getStore().getName().equals(storeName))
            {
                return sb.getStore();
            }
        }
        return null;
    }
}