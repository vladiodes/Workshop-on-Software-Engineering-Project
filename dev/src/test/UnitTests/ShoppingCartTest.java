package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;
import main.Stores.Store;
import main.Users.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.Mocks.BusMock;

import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {

    Store st1, st2;
    ShoppingCart cart;
    double phonePrice = 50;
    int phoneQuantity = 500;
    String productName1 = "Phone";
    String productName2 = "IPhone";
    String storeName1 = "Samsung";
    String storeName2 = "Apple";
    @BeforeEach
    void Setup(){
        cart = new ShoppingCart();
        User founder = new User(false, "Founder123", "12345678");
        st1 = new Store(storeName1, founder );
        st1.addProduct(productName1, "Electronics", new LinkedList<>(), "good phone", phoneQuantity, phonePrice);
        st2 = new Store(storeName2, founder );
        st2.addProduct(productName2, "Electronics", new LinkedList<>(), "good phone", phoneQuantity, phonePrice);
    }

    @Test
    void addProductToCart() {
        assertTrue(cart.addProductToCart(st1, productName1, 1));
        HashMap<String, ShoppingBasket> baskets = cart.getBasketInfo();
        assertEquals(baskets.size(), 1);
        assertTrue(baskets.containsKey(storeName1));
    }

    @Test
    void addProductConcurrencyCheck() throws Exception{
        Thread[] ts = new Thread[phoneQuantity * 2];
        for (int i = 0; i < ts.length; i ++){
            if (i< ts.length / 2)
                ts[i] = new Thread( () -> cart.addProductToCart(st1, productName1, 1));
            else
                ts[i] = new Thread( () -> cart.addProductToCart(st2, productName2, 1));
            ts[i].start();
        }
        for (int i = 0; i < ts.length; i ++)
            ts[i].join();
        int actual = 0;
        for (Integer i : cart.getProducts().values())
            actual += i;
        assertEquals(phoneQuantity * 2, actual);

    }

    @Test
    void addProductToBadItem() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> cart.addProductToCart(st1, "Iphone", 1));
        HashMap<String, ShoppingBasket> baskets = cart.getBasketInfo();
        assertEquals(baskets.size(), 0);
        assertFalse(baskets.containsKey(storeName1));
    }

    @Test
    void removeProductFromCartCase1() {
        cart.addProductToCart(st1, productName1, 1);
        assertTrue(cart.RemoveProductFromCart(st1, productName1,(int) Math.floor(10 * Math.random() + 1)));
        HashMap<String, ShoppingBasket> baskets = cart.getBasketInfo();
        assertEquals(baskets.size(), 0);
        assertFalse(baskets.containsKey(storeName1));
    }

    @Test
    void removeProductFromCartCase2() {
        cart.addProductToCart(st1, productName1, 2);
        assertTrue(cart.RemoveProductFromCart(st1, productName1,1));
        HashMap<String, ShoppingBasket> baskets = cart.getBasketInfo();
        assertEquals(baskets.size(), 1);
        assertTrue(baskets.containsKey(storeName1));
    }

    @Test
    void removeProductFromCartBadCase() {
        assertThrows(IllegalArgumentException.class, () -> cart.RemoveProductFromCart(st1, productName1,1));
        HashMap<String, ShoppingBasket> baskets = cart.getBasketInfo();
        assertEquals(baskets.size(), 0);
        assertFalse(baskets.containsKey(storeName1));
    }

    @Test
    void isProductInCart() {
        cart.addProductToCart(st1, productName1, 1);
        assertTrue(cart.isProductInCart(productName1, storeName1));
    }

    @Test
    void getPrice() {
        int amountOfPhones = (int) Math.floor(phoneQuantity * Math.random());
        double Expected = amountOfPhones * phonePrice;
        cart.addProductToCart(st1, productName1, amountOfPhones);
        assertEquals(Expected, cart.getPrice());
    }

    @Test
    void getAmountOfProducts() {
        int amountOfPhones = (int) Math.floor((phoneQuantity - 3) * Math.random());
        cart.addProductToCart(st1, productName1, amountOfPhones);
        assertEquals(1, cart.getAmountOfProducts());
        cart.addProductToCart(st1, productName1, 3);
        assertEquals(1, cart.getAmountOfProducts());
    }

    @Test
    void validateCart() {
        cart.addProductToCart(st1, productName1, 1);
        assertTrue(cart.ValidateCart());
    }

    @Test
    void validateCartNoItems() {
        assertFalse(cart.ValidateCart());
    }

    @Test
    void validateCartNotEnoughQuantity() {
        cart.addProductToCart(st1, productName1, phoneQuantity);
        st1.getProductsByName().get(productName1).subtractQuantity(5);
        assertFalse(cart.ValidateCart());
    }

    @Test
    void validateCartStoreNoLongerOpen() {
        cart.addProductToCart(st1, productName1, phoneQuantity);
        st1.closeStore(new BusMock());
        assertFalse(cart.ValidateCart());
    }
}