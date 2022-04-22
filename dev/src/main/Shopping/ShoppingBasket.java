package main.Shopping;

import main.Stores.Product;
import main.Stores.Store;

import java.util.HashMap;

public class ShoppingBasket {
    private HashMap<Product,Integer> productsQuantity;
    private ShoppingCart cart;
    private Store store;


    public ShoppingBasket(){
        productsQuantity=new HashMap<>();
    }

    public HashMap<Product,Integer> getProductsAndQuantities() {
        return productsQuantity;
    }

    public Store getStore() {
        return  this.store;
    }

    public HashMap<Product, Integer> getProductsQuantity() {
        return productsQuantity;
    }
}