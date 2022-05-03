package test.UnitTests;

import main.Shopping.ShoppingBasket;
import main.Stores.Discounts.ConditionalDiscount;
import main.Stores.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConditionalDiscountTest {

    @Mock
    ShoppingBasket shoppingBasket;
    @Mock
    Product productMock1;
    @Mock
    Product productMock2;

    double cleanPrice = 50;
    double highDiscount = 0.5;
    double lowDiscount = 0.3;
    @BeforeEach
    void setUp() {
        shoppingBasket = mock(ShoppingBasket.class);
        productMock1 = mock(Product.class);
        productMock2 = mock(Product.class);
        when(productMock1.getCleanPrice()).thenReturn(cleanPrice);
    }

    @Test
    void calculateDiscountHighestDiscount() {
        ConditionalDiscount discount = new ConditionalDiscount(getRestrictions(), LocalDate.MAX);
        when(shoppingBasket.hasAmount(any(Product.class),any(Integer.class))).thenReturn(true);
        Assertions.assertEquals(cleanPrice * (1 - highDiscount), discount.getPriceFor(productMock1, shoppingBasket));
    }

    @Test
    void calculateDiscountLowestDiscount() {
        ConditionalDiscount discount = new ConditionalDiscount(getRestrictions(), LocalDate.MAX);
        when(shoppingBasket.hasAmount(productMock1,2)).thenReturn(true);
        when(shoppingBasket.hasAmount(productMock2,1)).thenReturn(false);
        Assertions.assertEquals(cleanPrice * (1 - lowDiscount), discount.getPriceFor(productMock1, shoppingBasket));
    }

    @Test
    void calculateDiscountNoRestrictionsMet() {
        ConditionalDiscount discount = new ConditionalDiscount(getRestrictions(), LocalDate.MAX);
        when(shoppingBasket.hasAmount(any(Product.class),any(Integer.class))).thenReturn(false);
        Assertions.assertEquals(cleanPrice , discount.getPriceFor(productMock1, shoppingBasket));
    }

    @Test
    void calculateDiscountPastTime() {
        ConditionalDiscount discount = new ConditionalDiscount(getRestrictions(), LocalDate.MIN);
        when(shoppingBasket.hasAmount(any(Product.class),any(Integer.class))).thenReturn(true);
        Assertions.assertEquals(cleanPrice , discount.getPriceFor(productMock1, shoppingBasket));
    }



    //2 prodcutMock1 => lowDiscount
    //1 productMock1 & 1 productMock2 => highDiscount
    private HashMap<HashMap<Product, Integer>, Double> getRestrictions() {
        HashMap<HashMap<Product, Integer>, Double> restrictions = new HashMap<>();
        HashMap<Product, Integer> rest1 = new HashMap<>();
        rest1.put(productMock1, 2);
        double rest1discount = lowDiscount;
        restrictions.put(rest1, rest1discount);
        HashMap<Product, Integer> rest2 = new HashMap<>();
        rest2.put(productMock1, 1);
        rest2.put(productMock2, 1);
        double rest2discount = highDiscount;
        restrictions.put(rest2, rest2discount);
        return restrictions;
    }
}