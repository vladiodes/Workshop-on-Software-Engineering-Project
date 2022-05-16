package main.DTO;

import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;
import main.Users.User;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCartDTO {
    private HashMap<String, ShoppingBasketDTO> baskets;
    private double totalPrice;
    public ShoppingCartDTO(ShoppingCart cart, User user) {
        baskets = new HashMap<>();
        for (Map.Entry<String, ShoppingBasket> kv : cart.getBasketInfo().entrySet()){
            baskets.put(kv.getKey(), new ShoppingBasketDTO(kv.getValue(), user));
        }
        totalPrice=cart.getPrice();
    }

    public HashMap<String, ShoppingBasketDTO> getBaskets() {
        return baskets;
    }

    public double getTotalPrice() {
        return totalPrice;
    }


    public boolean isProductInHistory(String productName, String storeName)
    {
        if(!this.baskets.containsKey(storeName))
            return false;
        return this.baskets.get(storeName).isProductInHistory(productName);
    }
    public boolean isStoreInHistory(String storeName)
    {
        return this.baskets.containsKey(storeName);
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(String basket: baskets.keySet()){
            builder.append("From store " + basket + ":");
            for(ProductDTO p : baskets.get(basket).getProductsQuantity().keySet()){
                builder.append("Bought " + p.getProductName() + " X" + String.valueOf(baskets.get(basket).getProductsQuantity().get(p)));
            }
        }
        return builder.toString();
    }

}
