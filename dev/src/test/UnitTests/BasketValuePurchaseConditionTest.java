package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.SimpleConditions.BasketValuePurchaseCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BasketValuePurchaseConditionTest {
    private BasketValuePurchaseCondition subject;
    @Mock
    ShoppingBasket basketMock;
    double requiredAmount = 100 * Math.random();

    @BeforeEach
    void setUp() {
        basketMock = mock(ShoppingBasket.class);
        subject = new BasketValuePurchaseCondition(requiredAmount);
        when(basketMock.getCleanPrice()).thenReturn( requiredAmount + 1);
    }

    @Test
    void passAllows() {
        assertTrue(subject.pass(basketMock));
        verify(basketMock, times(1)).getCleanPrice();
    }

    @Test
    void passDisallows() {
        when(basketMock.getCleanPrice()).thenReturn( requiredAmount - 1);
        assertFalse(subject.pass(basketMock));
        verify(basketMock, times(1)).getCleanPrice();
    }
}