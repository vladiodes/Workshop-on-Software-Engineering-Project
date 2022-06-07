package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalAndPurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalOrPurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.PurchaseCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class LogicalOrPurchaseConditionTest {

    @Mock
    ShoppingBasket basketMock;
    LogicalOrPurchaseCondition subject;
    @Mock
    PurchaseCondition conditionMock1;
    @Mock
    PurchaseCondition conditionMock2;
    @BeforeEach
    void setUp() {
        basketMock = mock(ShoppingBasket.class);
        subject = new LogicalOrPurchaseCondition();
        conditionMock1 = mock(PurchaseCondition.class);
        conditionMock2 = mock(PurchaseCondition.class);
        when(conditionMock1.pass(basketMock)).thenReturn(true);
        when(conditionMock2.pass(basketMock)).thenReturn(true);
        subject.addCondition(conditionMock1);
        subject.addCondition(conditionMock2);
    }

    @Test
    void passAllows() {
        assertTrue(subject.pass(basketMock));
        verify(conditionMock1, times(1)).pass(basketMock);
        verify(conditionMock2, times(1)).pass(basketMock);
    }
    @Test
    void passAllowsCase2() {
        when(conditionMock2.pass(basketMock)).thenReturn(false);
        assertTrue(subject.pass(basketMock));
        verify(conditionMock1, times(1)).pass(basketMock);
        verify(conditionMock2, times(1)).pass(basketMock);
    }

    @Test
    void passDisallows() {
        when(conditionMock1.pass(basketMock)).thenReturn(false);
        when(conditionMock2.pass(basketMock)).thenReturn(false);
        assertFalse(subject.pass(basketMock));
        verify(conditionMock1, times(1)).pass(basketMock);
        verify(conditionMock2, times(1)).pass(basketMock);
    }
}