package main.DTO;

import main.Persistence.DAO;
import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Users.User;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
public class ShoppingBasketDTO {
    @Id
    @GeneratedValue
    private int id;

    @ElementCollection
    private Map<ProductDTO,Integer> productsQuantity;
    private String StoreName;
    private double totalPrice;
    public ShoppingBasketDTO(ShoppingBasket basket, User user) {
        StoreName = basket.getStore().getName();
        productsQuantity = new HashMap<>();
        for (Map.Entry<Product, Integer> kv : basket.getProductsAndQuantities().entrySet()) {
            productsQuantity.put(new ProductDTO(kv.getKey(), user), kv.getValue().intValue());
        }
        totalPrice=basket.getPrice();
    }
    public ShoppingBasketDTO(ShoppingBasket basket, User user,boolean toPersist) {
        StoreName = basket.getStore().getName();
        productsQuantity = new HashMap<>();
        for (Map.Entry<Product, Integer> kv : basket.getProductsAndQuantities().entrySet()) {
            ProductDTO p = new ProductDTO(kv.getKey());
            productsQuantity.put(p, kv.getValue().intValue());
            DAO.getInstance().persist(p);
        }
        totalPrice=basket.getPrice();
    }

    public ShoppingBasketDTO() {

    }

    public Map<ProductDTO, Integer> getProductsQuantity() {
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
