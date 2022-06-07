package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.SecretDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecretDiscountTest {

    @Mock
    ShoppingBasket basketMock;
    SecretDiscount subject;
    String real_password;
    @BeforeEach
    void setUp(){
        real_password = "pass";
        basketMock = mock(ShoppingBasket.class);
        subject = new SecretDiscount(LocalDate.MAX, 0.5, real_password);
    }

    @Test
    void isEligibleAllows() {
        when(basketMock.hasDiscountPassword(real_password)).thenReturn(true);
        assertTrue(subject.isEligible(basketMock));
    }

    @Test
    void isEligibleDisallows() {
        when(basketMock.hasDiscountPassword(real_password)).thenReturn(false);
        assertFalse(subject.isEligible(basketMock));
    }
}