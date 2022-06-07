package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalOrPurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.CompositeConditions.LogicalXorPurchaseCondition;
import main.Stores.PurchasePolicy.Conditions.PurchaseCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class LogicalXorPurchaseConditionTest {
    @Mock
    ShoppingBasket basketMock;
    LogicalXorPurchaseCondition subject;
    @Mock
    PurchaseCondition conditionMock1;
    @Mock
    PurchaseCondition conditionMock2;
    @BeforeEach
    void setUp() {
        basketMock = mock(ShoppingBasket.class);
        subject = new LogicalXorPurchaseCondition();
        conditionMock1 = mock(PurchaseCondition.class);
        conditionMock2 = mock(PurchaseCondition.class);
        when(conditionMock1.pass(basketMock)).thenReturn(true);
        when(conditionMock2.pass(basketMock)).thenReturn(false);
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
        when(conditionMock1.pass(basketMock)).thenReturn(false);
        when(conditionMock2.pass(basketMock)).thenReturn(true);
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

    @Test
    void passDisallowsCase2() {
        when(conditionMock1.pass(basketMock)).thenReturn(true);
        when(conditionMock2.pass(basketMock)).thenReturn(true);
        assertFalse(subject.pass(basketMock));
        verify(conditionMock1, times(1)).pass(basketMock);
        verify(conditionMock2, times(1)).pass(basketMock);
    }

    @Test
    void only2Conditions() {
        PurchaseCondition conditionMock3 = mock(PurchaseCondition.class);
        assertThrows(IllegalArgumentException.class, ()->subject.addCondition(conditionMock3));
    }
}