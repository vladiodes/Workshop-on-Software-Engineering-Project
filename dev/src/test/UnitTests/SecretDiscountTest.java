package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.Discounts.SecretDiscount;
import main.Stores.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecretDiscountTest {
    @Mock
    ShoppingBasket shoppingBasket;
    @Mock
    Product productMock1;
    String secretCode = "PewDiePie";
    double cleanPrice = 50;
    double DiscountAmount = 0.5;
    @BeforeEach
    void setUp() {
        shoppingBasket = mock(ShoppingBasket.class);
        productMock1 = mock(Product.class);
        when(productMock1.getCleanPrice()).thenReturn(cleanPrice);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void calculateDiscountHappy() {
        SecretDiscount discount = new SecretDiscount(DiscountAmount, LocalDate.MAX, secretCode);
        when(shoppingBasket.hasDiscountPassword(secretCode)).thenReturn(true);
        Assertions.assertEquals((1-DiscountAmount) * cleanPrice, discount.getPriceFor(cleanPrice, shoppingBasket));
    }

    @Test
    void calculateDiscountNoSecretCode() {
        SecretDiscount discount = new SecretDiscount(DiscountAmount, LocalDate.MAX, secretCode);
        when(shoppingBasket.hasDiscountPassword(secretCode)).thenReturn(false);
        Assertions.assertEquals(cleanPrice, discount.getPriceFor(cleanPrice, shoppingBasket));
    }

    @Test
    void calculateDiscountPastTime() {
        SecretDiscount discount = new SecretDiscount(DiscountAmount, LocalDate.MIN, secretCode);
        when(shoppingBasket.hasDiscountPassword(secretCode)).thenReturn(true);
        Assertions.assertEquals(cleanPrice, discount.getPriceFor(cleanPrice, shoppingBasket));
    }


}