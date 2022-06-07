package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.SimpleDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SimpleDiscountTest {
    @Mock
    ShoppingBasket basketMock;
    SimpleDiscount subject;
    Double percent = 0.3;
    @BeforeEach
    void setUp(){
        basketMock = mock(ShoppingBasket.class);
        subject = new SimpleDiscount(LocalDate.MAX, percent);
    }

    @Test
    void isEligible() {
        assertTrue(subject.isEligible(basketMock));
    }

    @Test
    void percent(){
        double actualPrice = 100.0;
        assertEquals(actualPrice * ( 1 - percent), subject.getPriceFor(actualPrice, basketMock));
    }
}