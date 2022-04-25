package main.DTO;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;

import java.util.HashMap;
import java.util.Map;


public class ShoppingBasketDTO {
    private HashMap<ProductDTO,Integer> productsQuantity;
    private String StoreName;
    public ShoppingBasketDTO(ShoppingBasket basket) {
        StoreName = basket.getStore().getName();
        productsQuantity = new HashMap<>();
        for (Map.Entry<Product, Integer> kv : basket.getProductsAndQuantities().entrySet())
            productsQuantity.put(new ProductDTO(kv.getKey()), kv.getValue().intValue());
    }

    public HashMap<ProductDTO, Integer> getProductsQuantity() {
        return productsQuantity;
    }

    public String getStoreName() {
        return StoreName;
    }
}
