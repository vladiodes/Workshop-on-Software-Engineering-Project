package main.DTO;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Users.User;

import java.util.HashMap;
import java.util.Map;


public class ShoppingBasketDTO {
    private HashMap<ProductDTO,Integer> productsQuantity;
    private String StoreName;
    private double totalPrice;
    public ShoppingBasketDTO(ShoppingBasket basket, User user) {
        StoreName = basket.getStore().getName();
        productsQuantity = new HashMap<>();
        for (Map.Entry<Product, Integer> kv : basket.getProductsAndQuantities().entrySet())
            productsQuantity.put(new ProductDTO(kv.getKey()), kv.getValue().intValue());
        totalPrice=basket.getPrice();
    }

    public HashMap<ProductDTO, Integer> getProductsQuantity() {
        return productsQuantity;
    }

    public String getStoreName() {
        return StoreName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public boolean isProductInHistory(String productName) {
        for(HashMap.Entry<ProductDTO, Integer> element : productsQuantity.entrySet())
        {
            if(element.getKey().getProductName().equals(productName))
                return true;
        }
        return false;
    }
}
