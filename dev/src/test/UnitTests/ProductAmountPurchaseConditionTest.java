package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.Product;
import main.Stores.PurchasePolicy.Conditions.SimpleConditions.ProductAmountPurchaseCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductAmountPurchaseConditionTest {
    @Mock
    ShoppingBasket basketMock;
    ProductAmountPurchaseCondition subject;
    int requiredAmount = (int) (Math.random() * 100);
    @Mock
    Product productMock;
    @BeforeEach
    void setUp() {
        basketMock = mock(ShoppingBasket.class);
        productMock = mock(Product.class);
        subject = new ProductAmountPurchaseCondition(requiredAmount, productMock);
        when(basketMock.hasAmount(productMock, requiredAmount)).thenReturn(true);
    }

    @Test
    void passAllows() {
        assertTrue(subject.pass(basketMock));
        verify(basketMock, times(1)).hasAmount(productMock, requiredAmount);
    }

    @Test
    void passDisallows() {
        when(basketMock.hasAmount(productMock, requiredAmount)).thenReturn(false);
        assertFalse(subject.pass(basketMock));
        verify(basketMock, times(1)).hasAmount(productMock, requiredAmount);
    }
}