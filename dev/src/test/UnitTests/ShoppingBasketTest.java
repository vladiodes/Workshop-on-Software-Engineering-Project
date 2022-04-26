package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Shopping.ShoppingCart;
import main.Stores.Product;
import main.Stores.Store;
import main.Users.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.Mocks.BusMock;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class ShoppingBasketTest {
    Store st1;
    ShoppingBasket basket;
    double phonePrice = 50;
    int phoneQuantity = 500;
    String productName1 = "Phone";
    String storeName1 = "Samsung";
    @BeforeEach
    void Setup(){
        User founder = new User(false, "Founder123", "12345678");
        st1 = new Store(storeName1, founder );
        st1.addProduct(productName1, "Electronics", new LinkedList<>(), "good phone", phoneQuantity, phonePrice);
        basket = new ShoppingBasket(st1);
    }

    @Test
    void addExistingProduct() {
        Assertions.assertTrue(basket.AddProduct(productName1, 1));
        Assertions.assertEquals(basket.getProductsAndQuantities().size(), 1);
        for (Product p : basket.getProductsAndQuantities().keySet())
            Assertions.assertEquals(st1.getProduct(productName1), p);
    }

    @Test
    void addNoneExistingProduct() {
        Assertions.assertThrows(IllegalArgumentException.class, ()->basket.AddProduct("NoneExisting", 1));
    }

    @Test
    void addBadQuantityProduct() {
        Assertions.assertThrows(IllegalArgumentException.class, ()->basket.AddProduct("NoneExisting", phoneQuantity + 5));
        Assertions.assertThrows(IllegalArgumentException.class, ()->basket.AddProduct("NoneExisting", 0));
        Assertions.assertThrows(IllegalArgumentException.class, ()->basket.AddProduct("NoneExisting", -1));
    }

    @Test
    void addClosedStoreProduct() {
        st1.closeStore(new BusMock());
        Assertions.assertThrows(IllegalArgumentException.class, ()->basket.AddProduct("NoneExisting", 1));
    }

    @Test
    void removeProduct() {
        basket.AddProduct(productName1, 1);
        Assertions.assertEquals(basket.RemoveProduct(productName1, (int) Math.floor(1 + 10 * Math.random())), 0);
        Assertions.assertEquals(0, basket.getAmountOfProducts());
    }

    @Test
    void removeNoneExistingProduct() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> basket.RemoveProduct("badName", 2));
        Assertions.assertEquals(0, basket.getAmountOfProducts());
    }

    @Test
    void removeBadQuantityProduct() {
        basket.AddProduct(productName1, 1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> basket.RemoveProduct(productName1, -1));
        Assertions.assertEquals(1, basket.getAmountOfProducts());
    }

    @Test
    void purchaseBasket() {
        int amountToBuy = 1;
        Product p = st1.getProduct(productName1);
        int prevAmount = p.getQuantity();
        basket.AddProduct(productName1, amountToBuy);
        basket.purchaseBasket(new BusMock());
        Assertions.assertEquals(prevAmount - amountToBuy, p.getQuantity());
    }

    @Test
    void getPrice() {
        Assertions.assertEquals(0, basket.getPrice());
        int quantity = (int) Math.floor(Math.random() * phoneQuantity);
        basket.AddProduct(productName1, quantity);
        double expected = quantity * phonePrice;
        Assertions.assertEquals(expected, basket.getPrice());
    }

    @Test
    void validateBasket() {
        int quantity = (int) Math.floor(Math.random() * phoneQuantity);
        basket.AddProduct(productName1, quantity);
        Assertions.assertTrue(basket.ValidateBasket());
    }

    @Test
    void validateBadQuantityBasket() {
        int quantity = phoneQuantity;
        basket.AddProduct(productName1, quantity);
        st1.getProduct(productName1).subtractQuantity(5);
        Assertions.assertFalse(basket.ValidateBasket());
    }

    @Test
    void validateClosedStoreBasket() {
        int quantity = (int) Math.floor(Math.random() * phoneQuantity);
        basket.AddProduct(productName1, quantity);
        st1.closeStore(new BusMock());
        Assertions.assertFalse(basket.ValidateBasket());
    }
}