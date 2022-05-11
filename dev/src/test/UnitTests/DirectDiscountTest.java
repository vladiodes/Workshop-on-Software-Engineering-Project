package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.SingleProductDiscounts.DirectDiscount;
import main.Stores.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DirectDiscountTest {

    @Mock
    ShoppingBasket shoppingBasket;
    @Mock
    Product productMock1;
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
        DirectDiscount discount = new DirectDiscount(DiscountAmount, LocalDate.MAX);
        Assertions.assertEquals((1-DiscountAmount) * cleanPrice, discount.getPriceFor(cleanPrice, shoppingBasket));
    }

    @Test
    void calculateDiscountPastTime() {
        DirectDiscount discount = new DirectDiscount(DiscountAmount, LocalDate.MIN);
        Assertions.assertEquals(cleanPrice, discount.getPriceFor(cleanPrice, shoppingBasket));
    }
}