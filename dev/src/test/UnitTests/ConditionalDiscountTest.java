package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.PurchaseCondition;
import main.Stores.PurchasePolicy.Discounts.CompositeDiscounts.PlusCompositeDiscount;
import main.Stores.PurchasePolicy.Discounts.Discount;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.ConditionalDiscount;
import org.hamcrest.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class ConditionalDiscountTest {
    ConditionalDiscount subject;
    @Mock
    ShoppingBasket basketMock;
    @Mock
    PurchaseCondition mockCondition;
    Double percent1 = 0.2;

    @BeforeEach
    void setup(){
        basketMock = mock(ShoppingBasket.class);
        mockCondition = mock(PurchaseCondition.class);
        subject = new ConditionalDiscount(LocalDate.MAX, percent1, mockCondition);
    }

    @Test
    void isEligibleAllows() {
        when(mockCondition.pass(any(ShoppingBasket.class))).thenReturn(true);
        assertTrue(subject.isEligible(basketMock));
        verify(mockCondition, times(1)).pass(basketMock);
    }

    @Test
    void isEligibleDisallows() {
        when(mockCondition.pass(any(ShoppingBasket.class))).thenReturn(false);
        assertFalse(subject.isEligible(basketMock));
        verify(mockCondition, times(1)).pass(basketMock);
    }
}