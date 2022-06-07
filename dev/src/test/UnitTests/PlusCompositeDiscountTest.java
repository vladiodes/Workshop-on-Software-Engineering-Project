package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.PurchasePolicy.Discounts.CompositeDiscounts.PlusCompositeDiscount;
import main.Stores.PurchasePolicy.Discounts.Discount;
import main.Stores.PurchasePolicy.Discounts.SimpleDiscounts.SimpleDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlusCompositeDiscountTest {

    PlusCompositeDiscount subject;
    @Mock
    ShoppingBasket basketMock;
    @Mock
    Discount mockDiscount1;
    @Mock
    Discount mockDiscount2;
    Double percent1 = 0.2;
    Double percent2 = 0.3;

    @BeforeEach
    void setup(){
        basketMock = mock(ShoppingBasket.class);
        mockDiscount1 = mock(Discount.class);
        mockDiscount2 = mock(Discount.class);
        when(mockDiscount1.getPercent(any(ShoppingBasket.class))).thenReturn(percent1);
        when(mockDiscount1.isEligible(any(ShoppingBasket.class))).thenReturn(true);
        when(mockDiscount2.getPercent(any(ShoppingBasket.class))).thenReturn(percent2);
        when(mockDiscount2.isEligible(any(ShoppingBasket.class))).thenReturn(true);
        subject = new PlusCompositeDiscount(LocalDate.MAX);
        subject.addDiscount(mockDiscount1);
        subject.addDiscount(mockDiscount2);
    }

    @Test
    void getPercentAllEligble() {
        assertEquals((percent1 + percent2), subject.getPercent(basketMock));
    }

    @Test
    void getPercentNotAllEligible() {
        when(mockDiscount2.isEligible(any(ShoppingBasket.class))).thenReturn(false);
        assertEquals(percent1, subject.getPercent(basketMock));
    }
    @Test
    void getPercentNothingEligible() {
        when(mockDiscount2.isEligible(any(ShoppingBasket.class))).thenReturn(false);
        when(mockDiscount1.isEligible(any(ShoppingBasket.class))).thenReturn(false);
        assertEquals(0, subject.getPercent(basketMock));
    }
}