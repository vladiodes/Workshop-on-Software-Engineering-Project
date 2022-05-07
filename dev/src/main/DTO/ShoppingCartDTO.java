package main.DTO;

import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;
import main.Stores.Store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShoppingCartDTO {
    private HashMap<String, ShoppingBasketDTO> baskets;
    private double totalPrice;
    public ShoppingCartDTO(ShoppingCart cart) {
        baskets = new HashMap<>();
        for (Map.Entry<String, ShoppingBasket> kv : cart.getBasketInfo().entrySet()){
            baskets.put(kv.getKey(), new ShoppingBasketDTO(kv.getValue()));
        }
        totalPrice=cart.getPrice();
    }

    public HashMap<String, ShoppingBasketDTO> getBaskets() {
        return baskets;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
